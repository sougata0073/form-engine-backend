package com.sougata.form_service.repository;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.question.response.LinearScaleResDto;
import com.sougata.form_service.model.LinearScale;
import org.springframework.stereotype.Repository;

@Repository("LINEAR_SCALE_REPOSITORY")
public interface LinearScaleRepository extends QuestionRepository<LinearScale, Long, LinearScaleResDto> {

    @Override
    default QuestionType getQuestionType() {
        return QuestionType.LINEAR_SCALE;
    }

    @Override
    default LinearScaleResDto toQuestionResDto(LinearScale linearScale) {
        return LinearScaleResDto.create(linearScale);
    }
}

