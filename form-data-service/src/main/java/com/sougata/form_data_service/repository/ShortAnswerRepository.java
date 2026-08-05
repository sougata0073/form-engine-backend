package com.sougata.form_data_service.repository;

import com.sougata.form_data_service.model.ShortAnswer;
import jakarta.persistence.Tuple;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository("SHORT_ANSWER_RESPONSE_REPOSITORY")
public interface ShortAnswerRepository extends AnyTypeQuestionResponseRepository<ShortAnswer, Long> {

    @Query("""
            select
            sa.questionResponse.questionId questionId,
            sa.text text
            from ShortAnswer sa
            where sa.questionResponse.formResponse.formId = :formId
            group by sa.questionResponse.questionId, sa.text
            order by count(sa.questionResponseId) desc, min(sa.questionResponse.formResponse.createdAt) asc
            """)
    List<Tuple> getResponsesTexts(UUID formId, Pageable pageable);

    @Query("""
            select
            sa.text
            from ShortAnswer sa
            where sa.questionResponse.formResponse.formId = :formId
            and sa.questionResponse.questionId = :questionId
            group by sa.text
            order by count(sa.questionResponseId) desc, min(sa.questionResponse.formResponse.createdAt) asc
            """)
    List<String> getResponseTexts(UUID formId, long questionId, Pageable pageable);

    @Query("""
            select
            sa.text text
            from ShortAnswer sa
            where sa.questionResponse.formResponse.formId = :formId and sa.questionResponse.questionId = :questionId
            group by sa.text
            order by count(sa.questionResponseId) desc
            """)
    List<Tuple> getAllResponseByQuestion(UUID formId, long questionId, Pageable pageable);

    @Query(value = """
            select
            count(distinct coalesce(sa.text, chr(1)))
            from form_responses fr
            left join question_responses qr
            on fr.id = qr.form_response_id and qr.question_id = :questionId
            left join short_answers sa
            on qr.id = sa.question_response_id
            where fr.form_id = :formId
            """, nativeQuery = true)
    Long getDistinctResponseCount(UUID formId, Long questionId);

    @Query(value = """
            select
            sa.text text,
            count(*) responseCount
            from form_responses fr
            left join question_responses qr
            on fr.id = qr.form_response_id
            and qr.question_id = :questionId
            left join short_answers sa
            on qr.id = sa.question_response_id
            where fr.form_id = :formId
            group by sa.text
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
            left join short_answers sa
            on qr.id = sa.question_response_id
            where fr.form_id = :formId and (
                (:response is null and sa.text is null)
                or sa.text = :response
            )
            order by fr.created_at
            """, nativeQuery = true)
    List<Tuple> getResponseIdsByGroupedResponse(UUID formId, long questionId, String response);
}
