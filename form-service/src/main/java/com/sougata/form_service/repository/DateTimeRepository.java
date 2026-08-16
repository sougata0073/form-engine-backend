package com.sougata.form_service.repository;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.model.DateTime;
import org.springframework.stereotype.Repository;

@Repository("DATE_TIME_REPOSITORY")
public interface DateTimeRepository extends AnyTypeQuestionRepository<DateTime, Long> {

    @Override
    default QuestionType getQuestionType() {
        return QuestionType.DATE_TIME;
    }

}


