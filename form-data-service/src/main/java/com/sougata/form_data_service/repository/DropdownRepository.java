package com.sougata.form_data_service.repository;

import com.sougata.form_data_service.model.Dropdown;
import jakarta.persistence.Tuple;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository("DROPDOWN_RESPONSE_REPOSITORY")
public interface DropdownRepository extends AnyTypeQuestionResponseRepository<Dropdown, Long> {

    @Query("""
            select
            dd.questionResponse.questionId questionId,
            dd.responseOptionId responseOptionId,
            count(dd.responseOptionId) responseCount
            from Dropdown dd
            where dd.questionResponse.formResponse.formId = :formId
            group by dd.responseOptionId, dd.questionResponse.questionId
            """)
    List<Tuple> getResponseOptionCount(UUID formId);

    @Query(value = """
            select
            count(distinct coalesce(dd.response_option_id, -1))
            from form_responses fr
            left join question_responses qr
            on fr.id = qr.form_response_id and qr.question_id = :questionId
            left join dropdowns dd
            on qr.id = dd.question_response_id
            where fr.form_id = :formId
            """, nativeQuery = true)
    Long getDistinctResponseCount(UUID formId, Long questionId);

    @Query(value = """
            select
            d.response_option_id optionId,
            count(*) responseCount
            from form_responses fr
            left join question_responses  qr
            on fr.id = qr.form_response_id
            and qr.question_id = :questionId
            left join dropdowns d
            on qr.id = d.question_response_id
            where fr.form_id = :formId
            group by d.response_option_id
            order by responseCount desc, d.response_option_id asc
            """, nativeQuery = true)
    List<Tuple> groupedByResponseOption(UUID formId, long questionId, Pageable pageable);

    @Query("""
            select
            d.questionResponse.questionId questionId,
            d.responseOptionId optionId
            from Dropdown d
            where d.questionResponse.formResponse.formId = :formId
            and d.questionResponse.formResponse.id = :formResponseId
            """)
    List<Tuple> getOptionIdsByFormResponse(UUID formId, long formResponseId);

    @Query(value = """
            select
            fr.id responseId,
            fr.user_id userId
            from form_responses fr
            left join question_responses qr
            on fr.id = qr.form_response_id
            and qr.question_id = :questionId
            left join dropdowns d
            on qr.id = d.question_response_id
            where fr.form_id = :formId and (
                (:response is null and d.response_option_id is null)
                or d.response_option_id = :response
            )
            order by fr.created_at
            """, nativeQuery = true)
    List<Tuple> getResponseIdsByGroupedResponse(UUID formId, long questionId, long response, Pageable pageable);
}
