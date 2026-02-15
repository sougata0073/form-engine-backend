package com.sougata.form_service.repository;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.question.response.TickBoxGridResDto;
import com.sougata.form_service.model.TickBoxGrid;
import org.springframework.stereotype.Repository;

@Repository("TICK_BOX_GRID_REPOSITORY")
public interface TickBoxGridRepository extends QuestionRepository<TickBoxGrid, Long, TickBoxGridResDto> {

    @Override
    default QuestionType getQuestionType() {
        return QuestionType.TICK_BOX_GRID;
    }

    @Override
    default TickBoxGridResDto toQuestionResDto(TickBoxGrid tickBoxGrid) {
        return TickBoxGridResDto.create(tickBoxGrid);
    }
}

