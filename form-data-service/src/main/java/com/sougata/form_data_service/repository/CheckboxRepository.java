package com.sougata.form_data_service.repository;

import com.sougata.form_data_service.model.Checkbox;
import jakarta.persistence.Tuple;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository("CHECKBOX_RESPONSE_REPOSITORY")
public interface CheckboxRepository extends AnyTypeQuestionResponseRepository<Checkbox, Long> {

    @Query("""
            select
            cb.questionResponse.questionId questionId,
            cbo.responseOptionId responseOptionId,
            count(cbo.responseOptionId) responseCount
            from CheckboxOption cbo
            join cbo.checkbox cb
            where cb.questionResponse.formResponse.formId = :formId
            group by cbo.responseOptionId, cb.questionResponse.questionId
            """)
    List<Tuple> getResponseOptionCount(UUID formId);

    @Query(value = """
        select count(*)
        from (
            select option_ids
            from (
                select
                    array_agg(co.response_option_id order by co.response_option_id) as option_ids
                from checkboxes c
                left join checkbox_options co
                    on co.checkbox_id = c.question_response_id
                join question_responses qr
                    on qr.id = c.question_response_id
                join form_responses fr
                    on fr.id = qr.form_response_id
                where qr.question_id = :questionId
                  and fr.form_id = :formId
                group by c.question_response_id
            ) responses
            group by option_ids
        ) t
        """, nativeQuery = true)
    long getDistinctResponseCount(UUID formId, long questionId);

    @Query(value = """
            select
                optionIds,
                count(*) as responseCount,
                array_agg(responseId order by createdAt) as responseIds
            from (
                select
                    fr.id as responseId,
                    fr.created_at as createdAt,
                    array_agg(co.response_option_id order by co.response_option_id) as optionIds
                from checkboxes c
                left join checkbox_options co
                    on co.checkbox_id = c.question_response_id
                join question_responses qr
                    on qr.id = c.question_response_id
                join form_responses fr
                    on fr.id = qr.form_response_id
                where qr.question_id = :questionId
                  and fr.form_id = :formId
                group by c.question_response_id, fr.id, fr.created_at
            ) responses
            group by optionIds
            order by responseCount desc
            """, nativeQuery = true)
    List<Tuple> groupedByResponseOptions(UUID formId, long questionId, Pageable pageable);

}
