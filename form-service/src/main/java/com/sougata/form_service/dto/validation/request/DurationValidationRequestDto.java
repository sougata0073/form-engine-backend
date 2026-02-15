package com.sougata.form_service.dto.validation.request;

import com.sougata.form_service.constant.ValidationRequestValidationMessages;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DurationValidationRequestDto extends ValidationRequest {

    @NotNull(message = ValidationRequestValidationMessages.HOURS_NOT_NULL)
    @Min(value = 0, message = ValidationRequestValidationMessages.INVALID_HOUR_RANGE)
    private Integer hours;

    @NotNull(message = ValidationRequestValidationMessages.MINUTES_NOT_NULL)
    @Min(value = 0, message = ValidationRequestValidationMessages.INVALID_MINUTE_RANGE)
    private Integer minutes;

    @NotNull(message = ValidationRequestValidationMessages.SECONDS_NOT_NULL)
    @Min(value = 0, message = ValidationRequestValidationMessages.INVALID_SECOND_RANGE)
    private Integer seconds;

}
