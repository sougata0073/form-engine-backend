package com.sougata.form_data_service.service.responseManager;

import com.sougata.form_data_service.constant.QuestionType;
import com.sougata.form_data_service.dto.question.request.FileUploadResponseAddReqDto;
import com.sougata.form_data_service.dto.question.response.FileUploadResDto;
import com.sougata.form_data_service.dto.response.question.FileUploadResponseQuestionDto;
import com.sougata.form_data_service.dto.response.summary.FileUploadResponseSummaryDto;
import com.sougata.form_data_service.model.FileUpload;
import com.sougata.form_data_service.model.FormResponse;
import com.sougata.form_data_service.repository.FileUploadRepository;
import com.sougata.form_data_service.repository.QuestionResponseRepository;
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
        FileUploadResponseQuestionDto.Summary
        > {

    private final FileUploadRepository fileUploadRepository;

    @Autowired
    public FileUploadManager(FileUploadRepository fileUploadRepository, QuestionResponseRepository questionResponseRepository) {
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

        var responseTextMap = fileUploadRepository.getResponseFiles(formId)
                .stream().collect(Collectors.groupingBy(e -> e.get("questionId", Long.class)));

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
                                    f.setResponses(
                                            responseTextMap.get(rs.questionId())
                                                    .stream()
                                                    .map(tuple -> new FileUploadResponseSummaryDto.Response(
                                                            tuple.get("fileName", String.class),
                                                            tuple.get("fileUrl", String.class),
                                                            tuple.get("fileMimeType", String.class)
                                                    ))
                                                    .toList()
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
    public FileUploadResponseQuestionDto.Summary getResponseByQuestionSummary(UUID formId, FileUploadResDto questionResponse) {
        var sum = new FileUploadResponseQuestionDto.Summary();

        sum.setQuestionId(questionResponse.getId());
        sum.setQuestion(questionResponse.getQuestion());
        sum.setQuestionType(questionResponse.getQuestionType());
        sum.setTotalResponseCount(getTotalResponseCount(formId, questionResponse.getId()));
        sum.setDistinctResponseCount(fileUploadRepository.getDistinctResponseCount(formId, questionResponse.getId()));

        return sum;
    }

    @Override
    public FileUploadResponseQuestionDto getResponseByQuestion(UUID formId, Long questionId, Map<String, String> extraParams, Pageable pageable) {
        var grouped = fileUploadRepository.groupedByFile(formId, questionId, pageable);

        var fu = new FileUploadResponseQuestionDto();

        var responses = grouped.stream().map(g -> {
            var res = new FileUploadResponseQuestionDto.Response();

            res.setFileName(g.get("fileName", String.class));
            res.setFileUrl(g.get("fileUrl", String.class));
            res.setFileMimeType(g.get("fileMimeType", String.class));
            res.setResponseCount(g.get("responseCount", Long.class));
            res.setResponseIds(Arrays.stream(g.get("responseIds", Long[].class)).map(Object::toString).toList());
                            
            return res;
        }).toList();

        fu.setResponses(responses);

        return fu;
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
