package com.sougata.form_service.repository;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.question.response.DateTimeResDto;
import com.sougata.form_service.model.DateTime;
import org.springframework.stereotype.Repository;

@Repository("DATE_TIME_REPOSITORY")
public interface DateTimeRepository extends QuestionRepository<DateTime, Long, DateTimeResDto> {

    @Override
    default QuestionType getQuestionType() {
        return QuestionType.DATE_TIME;
    }

    @Override
    default DateTimeResDto toQuestionResDto(DateTime dateTime) {
        return DateTimeResDto.create(dateTime);
    }
}


