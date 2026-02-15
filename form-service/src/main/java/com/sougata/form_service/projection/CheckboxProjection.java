package com.sougata.form_service.projection;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.question.response.CheckboxResDto;
import com.sougata.form_service.dto.validationConfig.ValidationConfig;
import com.sougata.form_service.exception.JsonParsingException;
import com.sougata.form_service.util.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Arrays;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CheckboxProjection implements QuestionProjection<CheckboxResDto> {
    private Long id;
    private String question;
    private String description;
    private Boolean required;
    private String[] options;
    private JsonNode validationConfig;
    private Integer orderIndex;

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.CHECKBOX;
    }

    @Override
    public CheckboxResDto getQuestionResponse() {
        try {
            return new CheckboxResDto(
                    id,
                    question,
                    description,
                    required,
                    Arrays.asList(options),
                    JsonUtil.oldJsonNodeToObject(validationConfig, ValidationConfig.class),
                    orderIndex,
                    QuestionType.CHECKBOX
            );
        } catch (JsonProcessingException e) {
            throw new JsonParsingException(JsonUtil.oldJsonNodeToString(validationConfig));
        }
    }
}
