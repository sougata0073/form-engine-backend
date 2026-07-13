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

}
