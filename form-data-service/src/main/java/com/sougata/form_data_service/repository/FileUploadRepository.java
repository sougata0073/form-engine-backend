package com.sougata.form_data_service.repository;

import com.sougata.form_data_service.model.FileUpload;
import jakarta.persistence.Tuple;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository("FILE_UPLOAD_RESPONSE_REPOSITORY")
public interface FileUploadRepository extends AnyTypeQuestionResponseRepository<FileUpload, Long> {

    @Query("""
            select
            f.questionResponse.questionId questionId,
            f.fileName fileName,
            f.fileUrl fileUrl,
            f.fileMimeType fileMimeType
            from FileUpload f
            where f.questionResponse.formResponse.formId = :formId
            """)
    List<Tuple> getResponseFiles(UUID formId);

    @Query("""
            select
            count(distinct (f.fileName, f.fileUrl, f.fileMimeType))
            from FileUpload f
            where f.questionResponse.questionId = :questionId and f.questionResponse.formResponse.formId = :formId
            """)
    Long getDistinctResponseCount(UUID formId, Long questionId);

    @Query(value = """
            select
            f.file_name fileName,
            f.file_url fileUrl,
            f.file_mime_type fileMimeType,
            count(f.question_response_id) responseCount,
            array_agg(fr.id order by fr.created_at) responseIds
            from file_uploads f
            join question_responses qr
            on qr.id = f.question_response_id
            join form_responses fr
            on qr.form_response_id = fr.id
            where fr.form_id = :formId and qr.question_id = :questionId
            group by f.file_name, f.file_url, f.file_mime_type
            order by responseCount desc
            """, nativeQuery = true)
    List<Tuple> groupedByFile(UUID formId, long questionId, Pageable pageable);

}
