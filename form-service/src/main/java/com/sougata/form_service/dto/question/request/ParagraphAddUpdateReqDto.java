package com.sougata.form_service.dto.question.request;

import com.sougata.form_service.constant.ValidationMessages;
import com.sougata.form_service.validation.configuration.ValidationConfig;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ParagraphAddUpdateReqDto extends QuestionAddUpdateReq {

    @Valid
    @NotNull(message = ValidationMessages.VALIDATION_CONFIG_NOT_NULL)
    private ValidationConfig validationConfig;

}
