package com.sougata.form_service.repository;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.question.response.DateResDto;
import com.sougata.form_service.model.questionSchema.Date;
import org.springframework.stereotype.Repository;

@Repository("DATE_REPOSITORY")
public interface DateRepository extends QuestionRepository<Date, Long, DateResDto> {

    @Override
    default QuestionType getQuestionType() {
        return QuestionType.DATE;
    }

    @Override
    default DateResDto toQuestionResDto(Date date) {
        return DateResDto.create(date);
    }
}

