package com.sougata.form_service.repository;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.question.response.DurationResDto;
import com.sougata.form_service.model.Duration;
import org.springframework.stereotype.Repository;

@Repository("DURATION_REPOSITORY")
public interface DurationRepository extends QuestionRepository<Duration, Long, DurationResDto> {

    @Override
    default QuestionType getQuestionType() {
        return QuestionType.DURATION;
    }

    @Override
    default DurationResDto toQuestionResDto(Duration duration) {
        return DurationResDto.create(duration);
    }
}

