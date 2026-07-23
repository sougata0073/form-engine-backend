package com.sougata.form_service.repository;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.question.response.DateResDto;
import com.sougata.form_service.model.questionSchema.Date;
import org.springframework.stereotype.Repository;

@Repository("DATE_REPOSITORY")
public interface DateRepository extends AnyTypeQuestionRepository<Date, Long, DateResDto> {

    @Override
    default QuestionType getQuestionType() {
        return QuestionType.DATE;
    }

}

