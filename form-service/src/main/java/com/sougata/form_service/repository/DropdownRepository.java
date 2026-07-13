package com.sougata.form_service.repository;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.question.response.DropdownResDto;
import com.sougata.form_service.model.questionSchema.Dropdown;
import org.springframework.stereotype.Repository;

@Repository("DROPDOWN_REPOSITORY")
public interface DropdownRepository extends QuestionRepository<Dropdown, Long, DropdownResDto> {

    @Override
    default QuestionType getQuestionType() {
        return QuestionType.DROPDOWN;
    }

    @Override
    default DropdownResDto toQuestionResDto(Dropdown dropdown) {
        return DropdownResDto.create(dropdown);
    }
}
