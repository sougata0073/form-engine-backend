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
            group by f.questionResponse.questionId, f.fileName, f.fileUrl, f.fileMimeType
            order by count(f.questionResponseId) desc
            """)
    List<Tuple> getResponsesFiles(UUID formId, Pageable pageable);

    @Query("""
            select
            f.fileName fileName,
            f.fileUrl fileUrl,
            f.fileMimeType fileMimeType
            from FileUpload f
            where f.questionResponse.formResponse.formId = :formId
            and f.questionResponse.questionId = :questionId
            group by f.fileName, f.fileUrl, f.fileMimeType
            order by count(f.questionResponseId) desc, min(f.questionResponse.formResponse.createdAt) asc
            """)
    List<Tuple> getResponseFiles(UUID formId, long questionId, Pageable pageable);

    @Query("""
            select
            f.fileName fileName,
            f.fileUrl fileUrl,
            f.fileMimeType fileMimeType
            from FileUpload f
            where f.questionResponse.formResponse.formId = :formId and f.questionResponse.questionId = :questionId
            group by f.fileName, f.fileUrl, f.fileMimeType
            order by count(f.questionResponseId) desc
            """)
    List<Tuple> getAllResponseByQuestion(UUID formId, long questionId, Pageable pageable);

    @Query(value = """
            select
            count(distinct (
                            coalesce(f.file_name, 'default_filename'), 
                            coalesce(f.file_url, 'default_url'),
                            coalesce(f.file_mime_type, 'default_mime_type')
                        )
            )
            from form_responses fr
            left join question_responses qr
            on fr.id = qr.form_response_id and qr.question_id = :questionId
            left join file_uploads f
            on qr.id = f.question_response_id
            where fr.form_id = :formId
            """, nativeQuery = true)
    Long getDistinctResponseCount(UUID formId, Long questionId);

    @Query(value = """
            select
            f.file_name fileName,
            f.file_url fileUrl,
            f.file_mime_type fileMimeType,
            count(*) responseCount
            from form_responses fr
            left join question_responses qr
            on fr.id = qr.form_response_id
            and qr.question_id = :questionId
            left join file_uploads f
            on qr.id = f.question_response_id
            where fr.form_id = :formId
            group by f.file_name, f.file_url, f.file_mime_type
            order by responseCount desc, min(fr.created_at) asc
            """, nativeQuery = true)
    List<Tuple> groupedByFile(UUID formId, long questionId, Pageable pageable);

    @Query("""
            select
            f.questionResponse.questionId questionId,
            f.fileName fileName,
            f.fileUrl fileUrl,
            f.fileMimeType fileMimeType
            from FileUpload f
            where f.questionResponse.formResponse.formId = :formId
            and f.questionResponse.formResponse.id = :formResponseId
            """)
    List<Tuple> getFileUploadsByFormResponse(UUID formId, long formResponseId);

}
