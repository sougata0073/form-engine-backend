package com.sougata.form_service.repository;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.question.response.ShortAnswerResDto;
import com.sougata.form_service.model.ShortAnswer;
import org.springframework.stereotype.Repository;

@Repository("SHORT_ANSWER_REPOSITORY")
public interface ShortAnswerRepository extends QuestionRepository<ShortAnswer, Long, ShortAnswerResDto> {

    @Override
    default QuestionType getQuestionType() {
        return QuestionType.SHORT_ANSWER;
    }

    @Override
    default ShortAnswerResDto toQuestionResDto(ShortAnswer shortAnswer) {
        return ShortAnswerResDto.create(shortAnswer);
    }
}

