package com.sougata.form_service.repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.question.response.ShortAnswerResDto;
import com.sougata.form_service.model.questionSchema.ShortAnswer;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("SHORT_ANSWER_REPOSITORY")
public interface ShortAnswerRepository extends QuestionRepository<ShortAnswer, Long, ShortAnswerResDto> {

    @Query("select s.validationConfig from ShortAnswer s where s.id = :id")
    Optional<JsonNode> getValidationConfig(Long id);

    @Override
    default QuestionType getQuestionType() {
        return QuestionType.SHORT_ANSWER;
    }

    @Override
    default ShortAnswerResDto toQuestionResDto(ShortAnswer shortAnswer) {
        return ShortAnswerResDto.create(shortAnswer);
    }
}

