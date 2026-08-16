package com.sougata.form_service.repository;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.model.Dropdown;
import org.springframework.stereotype.Repository;

@Repository("DROPDOWN_REPOSITORY")
public interface DropdownRepository extends AnyTypeQuestionRepository<Dropdown, Long> {

    @Override
    default QuestionType getQuestionType() {
        return QuestionType.DROPDOWN;
    }

}
