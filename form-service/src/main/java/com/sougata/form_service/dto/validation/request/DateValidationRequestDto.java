package com.sougata.form_service.dto.validation.request;

import com.sougata.form_service.constant.ValidationRequestValidationMessages;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DateValidationRequestDto extends ValidationRequest {

    @NotNull(message = ValidationRequestValidationMessages.DATE_VALUE_NOT_NULL)
    private Instant date;

}
