package com.sougata.form_service.service.questionManager;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.question.request.FileUploadAddUpdateReqDto;
import com.sougata.form_service.dto.question.response.FileTypeRes;
import com.sougata.form_service.dto.question.response.FileUploadResDto;
import com.sougata.form_service.dto.template.questionTemplate.FileUploadTemplateDetails;
import com.sougata.form_service.exception.FileTypeNotFoundException;
import com.sougata.form_service.exception.QuestionNotFoundException;
import com.sougata.form_service.model.FileType;
import com.sougata.form_service.model.FileUpload;
import com.sougata.form_service.model.Form;
import com.sougata.form_service.model.Question;
import com.sougata.form_service.repository.FileTypeRepository;
import com.sougata.form_service.repository.FileUploadRepository;
import com.sougata.form_service.repository.QuestionRepository;
import com.sougata.form_service.service.FormService;
import com.sougata.form_service.service.QuestionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service("FILE_UPLOAD_QUESTION_MANAGER")
public class FileUploadManager extends QuestionManager<FileUpload, FileUploadAddUpdateReqDto, FileUploadResDto, FileUploadTemplateDetails> {

    private final FileUploadRepository fileUploadRepository;
    private final FileTypeRepository fileTypeRepository;

    public FileUploadManager(FileUploadRepository fileUploadRepository, FileTypeRepository fileTypeRepository, FormService formService, QuestionRepository questionRepository) {
        super(questionRepository, formService);
        this.fileUploadRepository = fileUploadRepository;
        this.fileTypeRepository = fileTypeRepository;
    }

    @Override
    public FileUploadResDto get(UUID formId, Long questionId) {
        return toQuestionResDto(fileUploadRepository.findByQuestionId(questionId).orElseThrow(() -> new QuestionNotFoundException(questionId)));
    }

    @Override
    @Transactional(transactionManager = "schemaTransactionManager")
    public FileUploadResDto create(UUID formId, FileUploadAddUpdateReqDto crudDto) {
        var newFu = new FileUpload();

        var question = createQuestion(crudDto, formId);

        setPropertiesForNew(crudDto, newFu, question);

        var saved = fileUploadRepository.save(newFu);

        return toQuestionResDto(saved, question);
    }

    @Override
    @Transactional(transactionManager = "schemaTransactionManager")
    public FileUploadResDto create(UUID formId, Long questionId, FileUploadAddUpdateReqDto questionAddUpdateReq) {
        var newFu = new FileUpload();

        var question = updateQuestion(questionId, questionAddUpdateReq);

        setPropertiesForNew(questionAddUpdateReq, newFu, question);

        var saved = fileUploadRepository.save(newFu);

        return toQuestionResDto(saved, question);
    }

    @Override
    @Transactional(transactionManager = "schemaTransactionManager")
    public FileUploadResDto update(UUID formId, Long questionId, FileUploadAddUpdateReqDto questionAddUpdateReq) {
        FileUpload fu = fileUploadRepository.findByQuestionId(questionId)
                .orElseThrow(() -> new QuestionNotFoundException(QuestionType.FILE_UPLOAD, questionId));

        var question = updateQuestion(questionId, questionAddUpdateReq);

        List<String> categories = questionAddUpdateReq.getAllowedFileCategories();
        List<FileType> fileTypes = categories.stream()
                .map(category ->
                        fileTypeRepository.findByCategory(category)
                                .orElseThrow(() -> new FileTypeNotFoundException(category))
                ).collect(Collectors.toList());

        fu.getAllowedFileTypes().clear();

        fu.setMaxFileSize(questionAddUpdateReq.getMaxFileSize());
        fu.setAllowedFileTypes(fileTypes);

        fileUploadRepository.save(fu);

        return toQuestionResDto(fu, question);
    }

    @Override
    public FileUploadResDto toQuestionResDto(FileUpload childQuestion) {
        return toQuestionResDto(childQuestion, childQuestion.getQuestion());
    }

    @Override
    public FileUploadResDto toQuestionResDto(FileUpload childQuestion, Question parentQuestion) {
        var f = new FileUploadResDto();

        populateCommonFields(parentQuestion, f);

        f.setAllowedFileTypes(childQuestion.getAllowedFileTypes().stream()
                .map(ft -> new FileTypeRes(ft.getCategory(), Arrays.asList(ft.getMimeTypes())))
                .toList()
        );
        f.setMaxFileSize(childQuestion.getMaxFileSize());

        return f;
    }

    @Override
    public FileUploadAddUpdateReqDto toQuestionAddUpdateReq(FileUploadResDto questionRes) {
        var f = new FileUploadAddUpdateReqDto();

        populateCommonFields(questionRes, f);

        f.setAllowedFileCategories(
                questionRes.getAllowedFileTypes().stream()
                        .map(FileTypeRes::getCategory)
                        .toList()
        );
        f.setMaxFileSize(questionRes.getMaxFileSize());

        return f;
    }

    @Override
    @Transactional(transactionManager = "schemaTransactionManager")
    public FileUpload createFromTemplate(FileUploadTemplateDetails template, Form form) {
        var f = new FileUpload();

        f.setQuestion(createQuestionFromTemplate(template, form));
        f.setMaxFileSize(template.getMaxFileSize());
        f.setAllowedFileTypes(template.getAllowedFileTypes().stream().map(ft ->
                        fileTypeRepository.findByCategory(ft.getCategory())
                                .orElseThrow(() -> new FileTypeNotFoundException(ft.getCategory()))
                ).toList()
        );

        return fileUploadRepository.save(f);
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.FILE_UPLOAD;
    }

    @Override
    public void delete(UUID formId, Long questionId) {
        fileUploadRepository.deleteQuestion(questionId);
    }

    private void setPropertiesForNew(FileUploadAddUpdateReqDto source, FileUpload target, Question question) {
        List<String> categories = source.getAllowedFileCategories();
        List<FileType> fileTypes = categories.stream()
                .map(category ->
                        fileTypeRepository.findByCategory(category)
                                .orElseThrow(() -> new FileTypeNotFoundException(category))
                ).collect(Collectors.toList());

        target.setQuestion(question);
        target.setMaxFileSize(source.getMaxFileSize());
        target.setAllowedFileTypes(fileTypes);
    }
}
