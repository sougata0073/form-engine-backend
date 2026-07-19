package com.sougata.form_data_service.form_schema.repository;

import com.sougata.form_data_service.constant.QuestionType;
import com.sougata.form_data_service.form_schema.dto.questionSchema.response.FileUploadResDto;
import com.sougata.form_data_service.form_schema.model.FileUploadSchema;
import org.springframework.stereotype.Repository;

@Repository("FILE_UPLOAD_SCHEMA_REPOSITORY")
public interface FileUploadSchemaRepository extends QuestionSchemaRepository<FileUploadSchema, Long, FileUploadResDto> {

    @Override
    default QuestionType getQuestionType() {
        return QuestionType.FILE_UPLOAD;
    }
}

