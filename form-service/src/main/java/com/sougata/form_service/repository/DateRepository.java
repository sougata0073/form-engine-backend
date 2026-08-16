package com.sougata.form_service.repository;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.model.Date;
import org.springframework.stereotype.Repository;

@Repository("DATE_REPOSITORY")
public interface DateRepository extends AnyTypeQuestionRepository<Date, Long> {

    @Override
    default QuestionType getQuestionType() {
        return QuestionType.DATE;
    }

}

