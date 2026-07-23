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
}
