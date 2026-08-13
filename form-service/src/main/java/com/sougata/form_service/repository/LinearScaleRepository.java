package com.sougata.form_service.repository;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.question.response.LinearScaleResDto;
import com.sougata.form_service.model.LinearScale;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("LINEAR_SCALE_REPOSITORY")
public interface LinearScaleRepository extends AnyTypeQuestionRepository<LinearScale, Long, LinearScaleResDto> {

    @Query("select l.toNumber from LinearScale l where l.questionId = :id")
    Optional<Integer> getToNumber(Long id);

    @Override
    default QuestionType getQuestionType() {
        return QuestionType.LINEAR_SCALE;
    }

}

