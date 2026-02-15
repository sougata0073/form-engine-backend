package com.sougata.form_service.projection;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.question.response.ParagraphResDto;
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
public class ParagraphProjection implements QuestionProjection<ParagraphResDto> {
    private Long id;
    private String question;
    private String description;
    private Boolean required;
    private JsonNode validationConfig;
    private Integer orderIndex;

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.PARAGRAPH;
    }

    @Override
    public ParagraphResDto getQuestionResponse() {
        try {
            return new ParagraphResDto(
                    id,
                    question,
                    description,
                    required,
                    orderIndex,
                    QuestionType.PARAGRAPH,
                    JsonUtil.oldJsonNodeToObject(validationConfig, ValidationConfig.class)
            );
        } catch (JsonProcessingException e) {
            throw new JsonParsingException(JsonUtil.oldJsonNodeToString(validationConfig));
        }
    }
}
