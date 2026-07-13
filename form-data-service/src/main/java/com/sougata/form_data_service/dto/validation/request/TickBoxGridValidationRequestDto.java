package com.sougata.form_data_service.dto.validation.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TickBoxGridValidationRequestDto extends ValidationRequest {

    private List<TickBoxGridRowValidationRequestDto> rows;

}
