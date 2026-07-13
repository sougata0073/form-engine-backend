package com.sougata.form_data_service.service.responseManager;

import com.sougata.form_data_service.constant.QuestionType;
import com.sougata.form_data_service.dto.question.request.FileUploadResponseAddReqDto;
import com.sougata.form_data_service.dto.question.response.FileUploadResDto;
import com.sougata.form_data_service.dto.response.question.FileUploadResponseQuestionDto;
import com.sougata.form_data_service.dto.response.summary.FileUploadResponseSummaryDto;
import com.sougata.form_data_service.model.FileUpload;
import com.sougata.form_data_service.model.FormResponse;
import com.sougata.form_data_service.repository.FileUploadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service("FILE_UPLOAD_RESPONSE_MANAGER")
public class FileUploadManager extends ResponseManager<FileUploadResponseAddReqDto, FileUploadResponseSummaryDto, FileUploadResDto, FileUploadResponseQuestionDto> {

    private final FileUploadRepository fileUploadRepository;

    @Autowired
    public FileUploadManager(FileUploadRepository fileUploadRepository) {
        this.fileUploadRepository = fileUploadRepository;
    }

    @Override
    public void create(FileUploadResponseAddReqDto response, FormResponse formResponse) {
        FileUpload fileUpload = new FileUpload();
        fileUpload.setFileName(response.getFileName());
        fileUpload.setFileUrl(response.getFileUrl());
        fileUpload.setFileMimeType(response.getFileMimeType());
        fileUpload.setFileSize(response.getFileSize());
        fileUpload.setQuestionId(response.getQuestionId());
        fileUpload.setFormResponse(formResponse);

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
    public FileUploadResponseQuestionDto getResponseByQuestion(UUID formId, FileUploadResDto questionRes) {
        var grouped = fileUploadRepository.groupedByFile(formId, questionRes.getId());

        var totalResponseCount = grouped.stream()
                .mapToLong(g -> g.get("responseCount", Long.class).intValue())
                .sum();

        var sa = new FileUploadResponseQuestionDto();

        var responses = grouped.stream().map(g -> new FileUploadResponseQuestionDto.Response(
                g.get("fileName", String.class),
                g.get("fileUrl", String.class),
                g.get("fileMimeType", String.class),
                g.get("responseCount", Long.class).intValue(),
                Arrays.stream(g.get("responseIds", Long[].class)).map(Object::toString).toList()
        )).toList();

        sa.setQuestionId(questionRes.getId());
        sa.setQuestion(questionRes.getQuestion());
        sa.setQuestionType(questionRes.getQuestionType());
        sa.setResponses(responses);
        sa.setTotalResponseCount(totalResponseCount);

        return sa;
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.FILE_UPLOAD;
    }

    @Override
    public void deleteResponses(UUID formId, Long questionId) {
        var entities = fileUploadRepository.findByFormIdAndQuestionId(formId, questionId);

        fileUploadRepository.deleteAll(entities);
    }
}
