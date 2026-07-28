package com.sougata.form_data_service.repository;

import com.sougata.form_data_service.model.Paragraph;
import jakarta.persistence.Tuple;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository("PARAGRAPH_RESPONSE_REPOSITORY")
public interface ParagraphRepository extends AnyTypeQuestionResponseRepository<Paragraph, Long> {

    @Query("select p.questionResponse.questionId questionId, p.text text from Paragraph p where p.questionResponse.formResponse.formId = :formId")
    List<Tuple> getResponseTexts(UUID formId);

    @Query("""
            select
            count(distinct p.text)
            from Paragraph p
            where p.questionResponse.questionId = :questionId and p.questionResponse.formResponse.formId = :formId
            """)
    Long getDistinctResponseCount(UUID formId, Long questionId);

    @Query(value = """
            select
            p.text text,
            count(p.question_response_id) responseCount,
            array_agg(fr.id order by fr.created_at) responseIds
            from paragraphs p
            join question_responses qr
            on qr.id = p.question_response_id
            join form_responses fr
            on qr.form_response_id = fr.id
            where fr.form_id = :formId and qr.question_id = :questionId
            group by p.text
            order by responseCount desc
            """, nativeQuery = true)
    List<Tuple> groupedByText(UUID formId, long questionId, Pageable pageable);
}
