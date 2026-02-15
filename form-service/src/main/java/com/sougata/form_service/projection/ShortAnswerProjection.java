package com.sougata.form_service.projection;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.question.response.ShortAnswerResDto;
import com.sougata.form_service.dto.validationConfig.ValidationConfig;
import com.sougata.form_service.exception.JsonParsingException;
import com.sougata.form_service.util.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ShortAnswerProjection implements QuestionProjection<ShortAnswerResDto> {
    private Long id;
    private String question;
    private String description;
    private Boolean required;
    private JsonNode validationConfig;
    private Integer orderIndex;

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.SHORT_ANSWER;
    }

    @Override
    public ShortAnswerResDto getQuestionResponse() {
        try {
            return new ShortAnswerResDto(
                    id,
                    question,
                    description,
                    required,
                    orderIndex,
                    QuestionType.SHORT_ANSWER,
                    JsonUtil.oldJsonNodeToObject(validationConfig, ValidationConfig.class)
            );
        } catch (JsonProcessingException e) {
            throw new JsonParsingException(JsonUtil.oldJsonNodeToString(validationConfig));
        }
    }
}
