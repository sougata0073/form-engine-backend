package com.sougata.form_service.repository;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.question.response.CheckboxResDto;
import com.sougata.form_service.model.Checkbox;
import org.springframework.stereotype.Repository;

@Repository("CHECKBOX_REPOSITORY")
public interface CheckboxRepository extends AnyTypeQuestionRepository<Checkbox, Long, CheckboxResDto> {

    @Override
    default QuestionType getQuestionType() {
        return QuestionType.CHECKBOX;
    }

}
