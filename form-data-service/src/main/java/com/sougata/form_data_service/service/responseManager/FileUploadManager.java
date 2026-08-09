package com.sougata.form_data_service.service.responseManager;

import com.sougata.form_data_service.constant.QuestionType;
import com.sougata.form_data_service.dto.question.request.FileUploadResponseAddReqDto;
import com.sougata.form_data_service.dto.question.response.FileUploadResDto;
import com.sougata.form_data_service.dto.response.individual.DropdownResponseIndividualDto;
import com.sougata.form_data_service.dto.response.individual.FileUploadResponseIndividualDto;
import com.sougata.form_data_service.dto.response.question.FileUploadResponseQuestionDto;
import com.sougata.form_data_service.dto.response.summary.FileUploadResponseSummaryDto;
import com.sougata.form_data_service.feignClient.AuthServiceFeignClient;
import com.sougata.form_data_service.model.FileUpload;
import com.sougata.form_data_service.model.FormResponse;
import com.sougata.form_data_service.repository.FileUploadRepository;
import com.sougata.form_data_service.repository.FormResponseRepository;
import com.sougata.form_data_service.repository.QuestionResponseRepository;
import com.sougata.form_data_service.util.IdUtil;
import jakarta.persistence.Tuple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service("FILE_UPLOAD_RESPONSE_MANAGER")
public class FileUploadManager extends ResponseManager<
        FileUploadResponseAddReqDto,
        FileUploadResponseSummaryDto,
        FileUploadResDto,
        FileUploadResponseQuestionDto,
        FileUploadResponseQuestionDto.Response,
        FileUploadResponseQuestionDto.Summary,
        FileUploadResponseIndividualDto
        > {

    private final FileUploadRepository fileUploadRepository;

    @Autowired
    public FileUploadManager(FileUploadRepository fileUploadRepository, QuestionResponseRepository questionResponseRepository, FormResponseRepository formResponseRepository, AuthServiceFeignClient authServiceFeignClient) {
        super(questionResponseRepository);
        this.fileUploadRepository = fileUploadRepository;
    }

    @Override
    @Transactional
    public void create(FileUploadResponseAddReqDto response, FormResponse formResponse) {
        FileUpload fileUpload = new FileUpload();

        var qr = createQuestionResponse(response.getQuestionId(), formResponse);

        fileUpload.setFileName(response.getFileName());
        fileUpload.setFileUrl(response.getFileUrl());
        fileUpload.setFileMimeType(response.getFileMimeType());
        fileUpload.setFileSize(response.getFileSize());
        fileUpload.setQuestionResponse(qr);

        fileUploadRepository.save(fileUpload);
    }

    @Override
    public List<FileUploadResponseSummaryDto> getResponseSummaries(UUID formId, List<FileUploadResDto> questionResponses) {
        var responseSummaries = fileUploadRepository.getResponseSummaries(formId);
        var result = new ArrayList<FileUploadResponseSummaryDto>();

        questionResponses.forEach(qr ->
                result.add(
                        responseSummaries.stream()
                                .filter(rs -> Objects.equals(rs.questionId(), qr.getId()))
                                .map(rs -> {
                                    var f = new FileUploadResponseSummaryDto();

                                    f.setQuestionId(qr.getId());
                                    f.setQuestion(qr.getQuestion());
                                    f.setOrderIndex(qr.getOrderIndex());
                                    f.setNumberOfResponses(rs.numberOfResponses());
                                    f.setQuestionType(QuestionType.FILE_UPLOAD);

                                    var files = fileUploadRepository.getResponseFiles(formId, rs.questionId(), Pageable.ofSize(20));

                                    f.setResponses(
                                            files.stream().map(tuple ->
                                                    new FileUploadResponseSummaryDto.Response(
                                                            tuple.get("fileName", String.class),
                                                            tuple.get("fileUrl", String.class),
                                                            tuple.get("fileMimeType", String.class)
                                                    )
                                            ).toList()
                                    );

                                    return f;
                                })
                                .findFirst()
                                .orElseGet(() -> {
                                    var f = new FileUploadResponseSummaryDto();

                                    f.setQuestionId(qr.getId());
                                    f.setQuestion(qr.getQuestion());
                                    f.setOrderIndex(qr.getOrderIndex());
                                    f.setNumberOfResponses(0L);
                                    f.setQuestionType(QuestionType.FILE_UPLOAD);
                                    f.setResponses(List.of());

                                    return f;
                                })
                ));

        return result;
    }

    @Override
    public FileUploadResponseSummaryDto getResponseSummary(UUID formId, Long questionId, FileUploadResDto questionRes, Pageable pageable) {
        var responseSummary = fileUploadRepository.getResponseSummary(formId, questionId);
        var files = fileUploadRepository.getResponseFiles(formId, questionId, pageable);

        var f = new FileUploadResponseSummaryDto();

        f.setQuestionId(questionRes.getId());
        f.setQuestion(questionRes.getQuestion());
        f.setOrderIndex(questionRes.getOrderIndex());
        f.setNumberOfResponses(responseSummary.numberOfResponses());
        f.setQuestionType(getQuestionType());
        f.setResponses(
                files.stream().map(tuple ->
                        new FileUploadResponseSummaryDto.Response(
                                tuple.get("fileName", String.class),
                                tuple.get("fileUrl", String.class),
                                tuple.get("fileMimeType", String.class)
                        )
                ).toList()
        );

        return f;
    }

    @Override
    public FileUploadResponseQuestionDto.Summary getResponseByQuestionSummary(UUID formId, FileUploadResDto questionResponse) {
        return new FileUploadResponseQuestionDto.Summary();
    }

    @Override
    public FileUploadResponseQuestionDto getResponseByQuestion(UUID formId, Long questionId, Map<String, String> extraParams, Pageable pageable) {
        var grouped = fileUploadRepository.groupedByFile(formId, questionId, pageable);

        var fu = new FileUploadResponseQuestionDto();

        var responses = grouped.stream().map(g -> {
            var res = new FileUploadResponseQuestionDto.Response();

            res.setQuestionId(questionId);
            res.setQuestionType(getQuestionType());
            res.setFileName(g.get("fileName", String.class));
            res.setFileUrl(g.get("fileUrl", String.class));
            res.setFileMimeType(g.get("fileMimeType", String.class));
            res.setResponseCount(g.get("responseCount", Long.class));

            var map = new HashMap<String, List<String>>();

            map.put("fileName", List.of(res.getFileName() == null ? "" : res.getFileName()));
            map.put("fileUrl", List.of(res.getFileUrl() == null ? "" : res.getFileUrl()));
            map.put("fileMimeType", List.of(res.getFileMimeType() == null ? "" : res.getFileMimeType()));

            res.setFormResponsesIdentifier(IdUtil.generateCompressedEncodedId(map));

            return res;
        }).toList();

        fu.setQuestionId(questionId);
        fu.setQuestionType(getQuestionType());
        fu.setResponses(responses);

        return fu;
    }

    @Override
    public List<FileUploadResponseIndividualDto> getIndividualResponses(UUID formId, Long formResponseId) {
        var responses = fileUploadRepository.getFileUploadsByFormResponse(formId, formResponseId);

        return responses.stream().map(tuple -> {
            var qId = tuple.get("questionId", Long.class);
            var fileName = tuple.get("fileName", String.class);
            var fileUrl = tuple.get("fileUrl", String.class);
            var fileMimeType = tuple.get("fileMimeType", String.class);

            var res = new FileUploadResponseIndividualDto();

            res.setQuestionId(qId);
            res.setQuestionType(getQuestionType());
            res.setFileName(fileName);
            res.setFileUrl(fileUrl);
            res.setFileMimeType(fileMimeType);

            return res;
        }).toList();
    }

    @Override
    public List<Tuple> getFormResponseAndUserIds(UUID formId, Long questionId, String formResponsesIdentifier, Pageable pageable) {
        var map = IdUtil.reconstructCompressedEncodedId(formResponsesIdentifier);

        var fileName = map.get("fileName");
        var fileUrl = map.get("fileUrl");
        var fileMimeType = map.get("fileMimeType");

        if (fileName.isEmpty() || fileUrl.isEmpty() || fileMimeType.isEmpty()) {
            throw new IllegalArgumentException("Invalid Form Responses Identifier. Identifier: " + formResponsesIdentifier);
        }

        var fName = fileName.getFirst();
        var fUrl = fileUrl.getFirst();
        var fMimeType = fileMimeType.getFirst();

        return fileUploadRepository.getResponseIdsByGroupedResponse(formId, questionId, fName, fUrl, fMimeType, pageable);
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.FILE_UPLOAD;
    }

    @Override
    public void deleteResponses(UUID formId, Long questionId) {
        fileUploadRepository.deleteAllByFormIdAndQuestionId(formId, questionId);
    }
}
