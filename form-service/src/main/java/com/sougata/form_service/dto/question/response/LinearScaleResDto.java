package com.sougata.form_service.dto.question.response;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.model.questionSchema.LinearScale;
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
