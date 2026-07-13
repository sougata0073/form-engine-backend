package com.sougata.form_service.repository;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.question.response.TimeResDto;
import com.sougata.form_service.model.questionSchema.Time;
import org.springframework.stereotype.Repository;

@Repository("TIME_REPOSITORY")
public interface TimeRepository extends QuestionRepository<Time, Long, TimeResDto> {

    @Override
    default QuestionType getQuestionType() {
        return QuestionType.TIME;
    }

    @Override
    default TimeResDto toQuestionResDto(Time time) {
        return TimeResDto.create(time);
    }

}