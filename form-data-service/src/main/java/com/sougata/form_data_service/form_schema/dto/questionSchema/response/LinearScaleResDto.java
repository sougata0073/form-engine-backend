package com.sougata.form_data_service.form_schema.dto.questionSchema.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class LinearScaleResDto extends QuestionRes {
    private Integer fromNumber;
    private Integer toNumber;
}
