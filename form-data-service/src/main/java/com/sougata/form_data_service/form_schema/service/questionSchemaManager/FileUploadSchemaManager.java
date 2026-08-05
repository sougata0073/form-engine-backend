package com.sougata.form_data_service.form_schema.service.questionSchemaManager;

import com.sougata.form_data_service.constant.QuestionType;
import com.sougata.form_data_service.dto.question.request.FileUploadResponseAddReqDto;
import com.sougata.form_data_service.dto.question.response.FileTypeRes;
import com.sougata.form_data_service.dto.question.response.FileUploadResDto;
import com.sougata.form_data_service.form_schema.exception.InvalidFileSizeException;
import com.sougata.form_data_service.form_schema.exception.InvalidFileTypeException;
import com.sougata.form_data_service.form_schema.service.QuestionSchemaManager;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("FILE_UPLOAD_QUESTION_SCHEMA_MANAGER")
public class FileUploadSchemaManager extends QuestionSchemaManager<FileUploadResDto, FileUploadResponseAddReqDto> {

    @Override
    public boolean validateResponse(FileUploadResponseAddReqDto validationDto, FileUploadResDto fu) {
        if (validationDto.getFileSize() > fu.getMaxFileSize()) {
            throw new InvalidFileSizeException(validationDto.getFileSize(), fu.getMaxFileSize());
        }

        if (!fu.getAllowedFileTypes().isEmpty()) {
            List<String> mimeTypes = fu.getAllowedFileTypes()
                    .stream()
                    .map(FileTypeRes::getMimeTypes)
                    .flatMap(List::stream)
                    .toList();

            if (!mimeTypes.contains(validationDto.getFileMimeType())) {
                throw new InvalidFileTypeException(validationDto.getFileMimeType(), fu.getAllowedFileTypes().stream().map(FileTypeRes::getCategory).toList());
            }
        }

        return true;
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.FILE_UPLOAD;
    }

}
