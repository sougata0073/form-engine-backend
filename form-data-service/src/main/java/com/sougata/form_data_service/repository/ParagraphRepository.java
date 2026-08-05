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

    @Query("""
            select
            p.questionResponse.questionId questionId,
            p.text text
            from Paragraph p
            where p.questionResponse.formResponse.formId = :formId
            group by p.questionResponse.questionId, p.text
            order by count(p.questionResponseId) desc
            """)
    List<Tuple> getResponsesTexts(UUID formId, Pageable pageable);

    @Query("""
            select
            p.text
            from Paragraph p
            where p.questionResponse.formResponse.formId = :formId
            and p.questionResponse.questionId = :questionId
            group by p.text
            order by count(p.questionResponseId) desc, min(p.questionResponse.formResponse.createdAt) asc
            """)
    List<String> getResponseTexts(UUID formId, long questionId, Pageable pageable);

    @Query("""
            select
            p.text text
            from Paragraph p
            where p.questionResponse.formResponse.formId = :formId and p.questionResponse.questionId = :questionId
            group by p.text
            order by count(p.questionResponseId) desc
            """)
    List<Tuple> getAllResponseByQuestion(UUID formId, long questionId, Pageable pageable);

    @Query(value = """
            select
            count(distinct coalesce(p.text, chr(1)))
            from form_responses fr
            left join question_responses qr
            on fr.id = qr.form_response_id and qr.question_id = :questionId
            left join paragraphs p
            on qr.id = p.question_response_id
            where fr.form_id = :formId
            """, nativeQuery = true)
    Long getDistinctResponseCount(UUID formId, Long questionId);

    @Query(value = """
            select
            p.text text,
            count(*) responseCount
            from form_responses fr
            left join question_responses qr
            on fr.id = qr.form_response_id
            and qr.question_id = :questionId
            left join paragraphs p
            on qr.id = p.question_response_id
            where fr.form_id = :formId
            group by p.text
            order by responseCount desc, min(fr.created_at) asc
            """, nativeQuery = true)
    List<Tuple> groupedByText(UUID formId, long questionId, Pageable pageable);

    @Query(value = """
            select
            fr.id responseId,
            fr.user_id userId
            from form_responses fr
            left join question_responses qr
            on fr.id = qr.form_response_id
            and qr.question_id = :questionId
            left join paragraphs p
            on qr.id = p.question_response_id
            where fr.form_id = :formId and (
                (:response is null and p.text is null)
                or p.text = :response
            )
            order by fr.created_at
            """, nativeQuery = true)
    List<Tuple> getResponseIdsByGroupedResponse(UUID formId, long questionId, String response);
}
