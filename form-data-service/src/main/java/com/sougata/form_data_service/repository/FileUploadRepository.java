package com.sougata.form_data_service.repository;

import com.sougata.form_data_service.model.FileUpload;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository("FILE_UPLOAD_RESPONSE_REPOSITORY")
public interface FileUploadRepository extends QuestionResponseRepository<FileUpload, Long> {

    @Query("""
            select
            f.questionId questionId,
            f.fileName fileName,
            f.fileUrl fileUrl,
            f.fileMimeType fileMimeType
            from FileUpload f
            where f.formResponse.formId = :formId
            """)
    List<Tuple> getResponseFiles(UUID formId);

    @Query(value = """
            select
            f.file_name fileName,
            f.file_url fileUrl,
            f.file_mime_type fileMimeType,
            count(f.id) responseCount,
            array_agg(fr.id order by fr.created_at) responseIds
            from file_uploads f
            join form_responses fr
            on f.form_response_id = fr.id
            where fr.form_id = :formId and f.question_id = :questionId
            group by f.file_name, f.file_url, f.file_mime_type
            """, nativeQuery = true)
    List<Tuple> groupedByFile(UUID formId, Long questionId);

}
