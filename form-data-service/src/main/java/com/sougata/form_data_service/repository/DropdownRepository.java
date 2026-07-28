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

    @Query("""
            select
            count(distinct d.responseOptionId)
            from Dropdown d
            where d.questionResponse.questionId = :questionId and d.questionResponse.formResponse.formId = :formId
            """)
    Long getDistinctResponseCount(UUID formId, Long questionId);

    @Query(value = """
            select
                d.response_option_id as optionId,
                count(*) as responseCount,
                array_agg(fr.id order by fr.created_at) as responseIds
            from dropdowns d
            join question_responses qr
                on qr.id = d.question_response_id
            join form_responses fr
                on fr.id = qr.form_response_id
            where qr.question_id = :questionId
              and fr.form_id = :formId
            group by d.response_option_id
            order by responseCount desc
            """, nativeQuery = true)
    List<Tuple> groupedByResponseOption(UUID formId, long questionId, Pageable pageable);
}
