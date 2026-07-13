package com.sougata.form_data_service.dto.validation.request;

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

    private Instant date;

}
