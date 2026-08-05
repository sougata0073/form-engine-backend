package com.sougata.form_service.repository;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.question.response.FileUploadResDto;
import com.sougata.form_service.model.questionSchema.FileUpload;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository("FILE_UPLOAD_REPOSITORY")
public interface FileUploadRepository extends AnyTypeQuestionRepository<FileUpload, Long, FileUploadResDto> {

    @Modifying
    @Transactional
    @Query(value = """
            delete
            from file_upload_file_type ff
            where ff.file_upload_id = :fileUploadId
            """, nativeQuery = true)
    void deleteAllFileUploadFileTypeByFileUploadId(long fileUploadId);

    @Override
    default QuestionType getQuestionType() {
        return QuestionType.FILE_UPLOAD;
    }

}

