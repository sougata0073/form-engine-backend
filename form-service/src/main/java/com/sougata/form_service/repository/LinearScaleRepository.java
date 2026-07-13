package com.sougata.form_service.repository;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.question.response.LinearScaleResDto;
import com.sougata.form_service.model.questionSchema.LinearScale;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("LINEAR_SCALE_REPOSITORY")
public interface LinearScaleRepository extends QuestionRepository<LinearScale, Long, LinearScaleResDto> {

    @Query("select l.toNumber from LinearScale l where l.id = :id")
    Optional<Integer> getToNumber(Long id);

    @Override
    default QuestionType getQuestionType() {
        return QuestionType.LINEAR_SCALE;
    }

    @Override
    default LinearScaleResDto toQuestionResDto(LinearScale linearScale) {
        return LinearScaleResDto.create(linearScale);
    }
}

