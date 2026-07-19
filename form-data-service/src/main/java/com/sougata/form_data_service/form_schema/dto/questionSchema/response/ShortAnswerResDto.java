package com.sougata.form_data_service.form_schema.dto.questionSchema.response;

import com.sougata.form_data_service.dto.validationConfig.ValidationConfig;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ShortAnswerResDto extends QuestionRes {
    private ValidationConfig validationConfig;
}
