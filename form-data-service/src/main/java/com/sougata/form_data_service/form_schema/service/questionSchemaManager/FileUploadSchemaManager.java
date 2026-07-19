package com.sougata.form_data_service.form_schema.service.questionSchemaManager;

import com.sougata.form_data_service.constant.QuestionType;
import com.sougata.form_data_service.dto.question.request.FileUploadResponseAddReqDto;
import com.sougata.form_data_service.form_schema.dto.questionSchema.response.FileTypeRes;
import com.sougata.form_data_service.form_schema.dto.questionSchema.response.FileUploadResDto;
import com.sougata.form_data_service.form_schema.exception.InvalidFileSizeException;
import com.sougata.form_data_service.form_schema.exception.InvalidFileTypeException;
import com.sougata.form_data_service.form_schema.exception.QuestionSchemaNotFoundException;
import com.sougata.form_data_service.form_schema.model.FileType;
import com.sougata.form_data_service.form_schema.model.FileUploadSchema;
import com.sougata.form_data_service.form_schema.repository.FileUploadSchemaRepository;
import com.sougata.form_data_service.form_schema.service.QuestionSchemaManager;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service("FILE_UPLOAD_QUESTION_SCHEMA_MANAGER")
public class FileUploadSchemaManager extends QuestionSchemaManager<FileUploadSchema, FileUploadResDto, FileUploadResponseAddReqDto> {

    private final FileUploadSchemaRepository fileUploadSchemaRepository;

    public FileUploadSchemaManager(FileUploadSchemaRepository fileUploadSchemaRepository) {
        this.fileUploadSchemaRepository = fileUploadSchemaRepository;
    }

    @Override
    public FileUploadResDto get(UUID formId, Long questionId) {
        return toQuestionResDto(
                fileUploadSchemaRepository.findByFormIdAndId(formId, questionId)
                        .orElseThrow(() -> new QuestionSchemaNotFoundException(questionId))
        );
    }

    @Override
    public boolean validateResponse(FileUploadResponseAddReqDto validationDto) {
        FileUploadSchema fu = fileUploadSchemaRepository.findById(validationDto.getQuestionId())
                .orElseThrow(() -> new QuestionSchemaNotFoundException(QuestionType.FILE_UPLOAD, validationDto.getQuestionId()));

        if (validationDto.getFileSize() > fu.getMaxFileSize()) {
            throw new InvalidFileSizeException(validationDto.getFileSize(), fu.getMaxFileSize());
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
    public QuestionType getQuestionType() {
        return QuestionType.FILE_UPLOAD;
    }

    @Override
    public FileUploadResDto toQuestionResDto(FileUploadSchema questionSchema) {
        var f = new FileUploadResDto();

        populateCommonFields(questionSchema, f);

        f.setAllowedFileTypes(questionSchema.getAllowedFileTypes().stream()
                .map(ft -> new FileTypeRes(ft.getCategory(), Arrays.asList(ft.getMimeTypes())))
                .toList()
        );
        f.setMaxFileSize(questionSchema.getMaxFileSize());

        return f;
    }

}
