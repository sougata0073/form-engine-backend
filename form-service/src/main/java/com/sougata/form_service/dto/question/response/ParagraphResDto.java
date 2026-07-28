package com.sougata.form_service.dto.question.response;

import com.sougata.form_service.dto.validationConfig.ValidationConfig;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ParagraphResDto extends QuestionRes {
    private ValidationConfig validationConfig;
}
