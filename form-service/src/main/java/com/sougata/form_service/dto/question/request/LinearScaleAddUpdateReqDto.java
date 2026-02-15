package com.sougata.form_service.dto.question.request;

import com.sougata.form_service.constant.ValidationMessages;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class LinearScaleAddUpdateReqDto extends QuestionAddUpdateReq {

    @NotNull(message = ValidationMessages.FROM_NUMBER_NOT_NULL)
    private Integer fromNumber;

    @NotNull(message = ValidationMessages.TO_NUMBER_NOT_NULL)
    private Integer toNumber;

}
