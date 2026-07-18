package com.sougata.form_data_service.repository;

import com.sougata.form_data_service.model.Dropdown;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository("DROPDOWN_RESPONSE_REPOSITORY")
public interface DropdownRepository extends QuestionResponseRepository<Dropdown, Long> {

    @Query("""
            select
            dd.questionId questionId,
            dd.responseOptionId responseOptionId,
            count(dd.responseOptionId) responseCount
            from Dropdown dd
            where dd.formResponse.formId = :formId
            group by dd.responseOptionId, dd.questionId
            """)
    List<Tuple> getResponseOptionCount(UUID formId);

    @Query(value = """
            select
                d.response_option_id as optionId,
                count(*) as responseCount,
                array_agg(fr.id order by fr.created_at) as responseIds
            from dropdowns d
            join form_responses fr
                on fr.id = d.form_response_id
            where d.question_id = :questionId
              and fr.form_id = :formId
            group by d.response_option_id
            order by responseCount desc
            """, nativeQuery = true)
    List<Tuple> groupedByResponseOption(UUID formId, Long questionId);
}
