package com.sougata.form_service.dto.question.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.validationConfig.ValidationConfig;
import com.sougata.form_service.exception.JsonParsingException;
import com.sougata.form_service.model.questionSchema.Paragraph;
import com.sougata.form_service.util.JsonUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ParagraphResDto extends QuestionRes {
    private ValidationConfig validationConfig;

    public ParagraphResDto(Long id, String question, String description, Boolean required, Integer orderIndex, QuestionType questionType, ValidationConfig validationConfig) {
        super(id, question, description, required, orderIndex, questionType);
        this.validationConfig = validationConfig;
    }

    public static ParagraphResDto create(Paragraph paragraph) {
        try {
            return new ParagraphResDto(
                    paragraph.getId(),
                    paragraph.getQuestion(),
                    paragraph.getDescription(),
                    paragraph.getRequired(),
                    paragraph.getOrderIndex(),
                    QuestionType.PARAGRAPH,
                    JsonUtil.oldJsonNodeToObject(paragraph.getValidationConfig(), ValidationConfig.class)
            );
        } catch (JsonProcessingException e) {
            throw new JsonParsingException(JsonUtil.oldJsonNodeToString(paragraph.getValidationConfig()));
        }
    }
}
