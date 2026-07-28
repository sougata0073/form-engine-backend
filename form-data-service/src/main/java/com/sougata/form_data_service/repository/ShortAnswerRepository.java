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

    @Query("select sa.questionResponse.questionId questionId, sa.text text from ShortAnswer sa where sa.questionResponse.formResponse.formId = :formId")
    List<Tuple> getResponseTexts(UUID formId);

    @Query("""
            select
            count(distinct sa.text)
            from ShortAnswer sa
            where sa.questionResponse.questionId = :questionId and sa.questionResponse.formResponse.formId = :formId
            """)
    Long getDistinctResponseCount(UUID formId, Long questionId);

    @Query(value = """
            select
            sa.text text,
            count(sa.question_response_id) responseCount,
            array_agg(fr.id order by fr.created_at) responseIds
            from short_answers sa
            join question_responses qr
            on qr.id = sa.question_response_id
            join form_responses fr
            on qr.form_response_id = fr.id
            where fr.form_id = :formId and qr.question_id = :questionId
            group by sa.text
            order by responseCount desc
            """, nativeQuery = true)
    List<Tuple> groupedByText(UUID formId, long questionId, Pageable pageable);

}
