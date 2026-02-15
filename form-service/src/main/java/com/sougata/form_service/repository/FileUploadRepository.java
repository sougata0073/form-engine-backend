package com.sougata.form_service.repository;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.question.response.FileUploadResDto;
import com.sougata.form_service.model.FileUpload;
import org.springframework.stereotype.Repository;

@Repository("FILE_UPLOAD_REPOSITORY")
public interface FileUploadRepository extends QuestionRepository<FileUpload, Long, FileUploadResDto> {

    @Override
    default QuestionType getQuestionType() {
        return QuestionType.FILE_UPLOAD;
    }

    @Override
    default FileUploadResDto toQuestionResDto(FileUpload fileUpload) {
        return FileUploadResDto.create(fileUpload);
    }
}

