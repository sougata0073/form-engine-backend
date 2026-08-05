package com.sougata.form_service.service.questionManager;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.question.request.FileUploadAddUpdateReqDto;
import com.sougata.form_service.dto.question.response.FileTypeRes;
import com.sougata.form_service.dto.question.response.FileUploadResDto;
import com.sougata.form_service.exception.FileTypeNotFoundException;
import com.sougata.form_service.exception.QuestionNotFoundException;
import com.sougata.form_service.model.FileType;
import com.sougata.form_service.model.questionSchema.FileUpload;
import com.sougata.form_service.model.questionSchema.Question;
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
public class FileUploadManager extends QuestionManager<FileUpload, FileUploadAddUpdateReqDto, FileUploadResDto> {

    private final FileUploadRepository fileUploadRepository;
    private final FileTypeRepository fileTypeRepository;

    public FileUploadManager(FileUploadRepository fileUploadRepository, FileTypeRepository fileTypeRepository, FormService formService, QuestionRepository questionRepository) {
        super(questionRepository, formService);
        this.fileUploadRepository = fileUploadRepository;
        this.fileTypeRepository = fileTypeRepository;
    }

    @Override
    public FileUploadResDto get(UUID formId, Long questionId) {
        return toQuestionResDto(fileUploadRepository.findByQuestion_FormIdAndQuestion_Id(formId, questionId).orElseThrow(() -> new QuestionNotFoundException(questionId)));
    }

    @Override
    @Transactional
    public FileUploadResDto create(UUID formId, FileUploadAddUpdateReqDto crudDto) {
        var newFu = new FileUpload();

        var question = createQuestion(crudDto, formId);

        setPropertiesForNew(crudDto, newFu, question);

        var saved = fileUploadRepository.save(newFu);

        return toQuestionResDto(saved);
    }

    @Override
    public FileUploadResDto create(UUID formId, Long questionId, FileUploadAddUpdateReqDto crudDto) {
        var newFu = new FileUpload();

        var question = updateQuestion(questionId, crudDto);

        setPropertiesForNew(crudDto, newFu, question);

        var saved = fileUploadRepository.save(newFu);

        return toQuestionResDto(saved);
    }

    @Override
    @Transactional
    public FileUploadResDto update(Long questionId, FileUploadAddUpdateReqDto crudDto) {
        FileUpload fu = fileUploadRepository.findById(questionId)
                .orElseThrow(() -> new QuestionNotFoundException(QuestionType.FILE_UPLOAD, questionId));

        updateQuestion(questionId, crudDto);

        List<String> categories = crudDto.getAllowedFileCategories();
        List<FileType> fileTypes = categories.stream()
                .map(category ->
                        fileTypeRepository.findById(category)
                                .orElseThrow(() -> new FileTypeNotFoundException(category))
                ).collect(Collectors.toList());

        fu.getAllowedFileTypes().clear();

        fu.setMaxFileSize(crudDto.getMaxFileSize());
        fu.setAllowedFileTypes(fileTypes);

        fileUploadRepository.save(fu);

        return toQuestionResDto(fu);
    }

    @Override
    public FileUploadResDto toQuestionResDto(FileUpload question) {
        var f = new FileUploadResDto();

        populateCommonFields(question, f);

        f.setAllowedFileTypes(question.getAllowedFileTypes().stream()
                .map(ft -> new FileTypeRes(ft.getCategory(), Arrays.asList(ft.getMimeTypes())))
                .toList()
        );
        f.setMaxFileSize(question.getMaxFileSize());

        return f;
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.FILE_UPLOAD;
    }

    @Override
    public void delete(UUID formId, Long questionId) {
        fileUploadRepository.deleteAllFileUploadFileTypeByFileUploadId(questionId);
        fileUploadRepository.deleteQuestion(formId, questionId);
    }

    private void setPropertiesForNew(FileUploadAddUpdateReqDto source, FileUpload target, Question question) {
        List<String> categories = source.getAllowedFileCategories();
        List<FileType> fileTypes = categories.stream()
                .map(category ->
                        fileTypeRepository.findById(category)
                                .orElseThrow(() -> new FileTypeNotFoundException(category))
                ).collect(Collectors.toList());

        target.setQuestion(question);
        target.setMaxFileSize(source.getMaxFileSize());
        target.setAllowedFileTypes(fileTypes);
    }
}
