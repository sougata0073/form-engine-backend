package com.sougata.form_service.dto.validation.request;

import com.sougata.form_service.constant.ValidationRequestValidationMessages;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LinearScaleValidationRequestDto extends ValidationRequest {

    @NotNull(message = ValidationRequestValidationMessages.SCALE_NOT_NULL)
    private Integer scale;

}
