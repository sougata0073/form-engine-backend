package com.sougata.form_service.repository;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.model.LinearScale;
import org.springframework.stereotype.Repository;

@Repository("LINEAR_SCALE_REPOSITORY")
public interface LinearScaleRepository extends AnyTypeQuestionRepository<LinearScale, Long> {

    @Override
    default QuestionType getQuestionType() {
        return QuestionType.LINEAR_SCALE;
    }

}

