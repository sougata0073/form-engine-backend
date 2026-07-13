package com.sougata.form_service.repository;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.question.response.MultipleChoiceResDto;
import com.sougata.form_service.model.questionSchema.MultipleChoice;
import org.springframework.stereotype.Repository;

@Repository("MULTIPLE_CHOICE_REPOSITORY")
public interface MultipleChoiceRepository extends QuestionRepository<MultipleChoice, Long, MultipleChoiceResDto> {

    @Override
    default QuestionType getQuestionType() {
        return QuestionType.MULTIPLE_CHOICE;
    }

    @Override
    default MultipleChoiceResDto toQuestionResDto(MultipleChoice multipleChoice) {
        return MultipleChoiceResDto.create(multipleChoice);
    }
}

