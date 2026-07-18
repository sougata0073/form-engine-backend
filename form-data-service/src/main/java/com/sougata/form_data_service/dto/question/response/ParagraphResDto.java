package com.sougata.form_data_service.dto.question.response;

import com.sougata.form_data_service.dto.validationConfig.ValidationConfig;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class ParagraphResDto extends QuestionRes {
    private ValidationConfig validationConfig;

}
