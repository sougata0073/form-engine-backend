package com.sougata.form_service.service.questionManager;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.question.request.FileUploadAddUpdateReqDto;
import com.sougata.form_service.dto.question.response.FileUploadResDto;
import com.sougata.form_service.dto.validation.request.FileUploadValidationRequestDto;
import com.sougata.form_service.exception.FileTypeNotFoundException;
import com.sougata.form_service.exception.InvalidFileSizeException;
import com.sougata.form_service.exception.InvalidFileTypeException;
import com.sougata.form_service.exception.QuestionNotFoundException;
import com.sougata.form_service.model.FileType;
import com.sougata.form_service.model.FileUpload;
import com.sougata.form_service.repository.FileTypeRepository;
import com.sougata.form_service.repository.FileUploadRepository;
import com.sougata.form_service.service.FormService;
import com.sougata.form_service.service.QuestionManager;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service("FILE_UPLOAD_QUESTION_MANAGER")
public class FileUploadManager extends QuestionManager<FileUploadAddUpdateReqDto, FileUploadResDto, FileUploadValidationRequestDto> {

    private final FileUploadRepository fileUploadRepository;
    private final FileTypeRepository fileTypeRepository;
    private final FormService formService;

    public FileUploadManager(FileUploadRepository fileUploadRepository, FileTypeRepository fileTypeRepository, FormService formService) {
        this.fileUploadRepository = fileUploadRepository;
        this.fileTypeRepository = fileTypeRepository;
        this.formService = formService;
    }

    @Override
    public FileUploadResDto create(UUID formId, FileUploadAddUpdateReqDto crudDto) {
        FileUpload newFu = new FileUpload();

        setProperties(crudDto, formId, newFu);

        FileUpload saved = fileUploadRepository.save(newFu);

        return FileUploadResDto.create(saved);
    }

    @Override
    public FileUploadResDto create(UUID formId, Long questionId, FileUploadAddUpdateReqDto crudDto) {
        FileUpload newFu = new FileUpload();

        newFu.setId(questionId);
        setProperties(crudDto, formId, newFu);

        FileUpload saved = fileUploadRepository.save(newFu);

        return FileUploadResDto.create(saved);
    }

    @Override
    public FileUploadResDto update(Long questionId, FileUploadAddUpdateReqDto crudDto) {
        FileUpload fu = fileUploadRepository.findById(questionId)
                .orElseThrow(() -> new QuestionNotFoundException(QuestionType.FILE_UPLOAD, questionId));

        setProperties(crudDto, fu);

        fileUploadRepository.save(fu);

        return FileUploadResDto.create(fu);
    }

    @Override
    public boolean exists(Long questionId) {
        return fileUploadRepository.existsById(questionId);
    }

    @Override
    public boolean validateResponse(FileUploadValidationRequestDto validationDto) {
        FileUpload fu = fileUploadRepository.findById(validationDto.getQuestionId())
                .orElseThrow(() -> new QuestionNotFoundException(QuestionType.FILE_UPLOAD, validationDto.getQuestionId()));

        if (validationDto.getFileSizeInMb() > fu.getMaxFileSizeInMB()) {
            throw new InvalidFileSizeException(validationDto.getFileSizeInMb(), fu.getMaxFileSizeInMB());
        }

        if (fu.getAllowedFileTypes() != null) {
            List<String> mimeTypes = fu.getAllowedFileTypes()
                    .stream()
                    .map(FileType::getMimeTypes)
                    .flatMap(Arrays::stream)
                    .toList();

            if (!mimeTypes.contains(validationDto.getFileMimeType())) {
                throw new InvalidFileTypeException(validationDto.getFileMimeType(), fu.getAllowedFileTypes());
            }
        }

        return true;
    }

    @Override
    public Class<FileUploadAddUpdateReqDto> getCrudDtoClass() {
        return FileUploadAddUpdateReqDto.class;
    }

    @Override
    public Class<FileUploadValidationRequestDto> getValidationDtoClass() {
        return FileUploadValidationRequestDto.class;
    }

    @Override
    @SuppressWarnings("unchecked")
    public FileUploadRepository getQuestionRepository() {
        return fileUploadRepository;
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.FILE_UPLOAD;
    }

    private void setProperties(FileUploadAddUpdateReqDto source, UUID formId, FileUpload target) {
        target.setQuestion(source.getQuestion());
        target.setDescription(source.getDescription());
        target.setRequired(source.getRequired());
        target.setMaxFileSizeInMB(source.getMaxFileSizeInMB());
        target.setOrderIndex(source.getOrderIndex());

        List<String> categories = source.getAllowedFileCategories();
        List<FileType> fileTypes = categories.stream()
                .map(category ->
                        fileTypeRepository.findById(category)
                                .orElseThrow(() -> new FileTypeNotFoundException(category))
                ).collect(Collectors.toList());

        target.setAllowedFileTypes(fileTypes);

        if (formId != null) {
            target.setForm(formService.getFormById(formId));
        }
    }

    private void setProperties(FileUploadAddUpdateReqDto source, FileUpload target) {
        setProperties(source, null, target);
    }
}
