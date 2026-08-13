package com.sougata.form_service.repository;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.question.response.MultipleChoiceGridResDto;
import com.sougata.form_service.model.MultipleChoiceGrid;
import org.springframework.stereotype.Repository;

@Repository("MULTIPLE_CHOICE_GRID_REPOSITORY")
public interface MultipleChoiceGridRepository extends AnyTypeQuestionRepository<MultipleChoiceGrid, Long, MultipleChoiceGridResDto> {

    @Override
    default QuestionType getQuestionType() {
        return QuestionType.MULTIPLE_CHOICE_GRID;
    }

}

