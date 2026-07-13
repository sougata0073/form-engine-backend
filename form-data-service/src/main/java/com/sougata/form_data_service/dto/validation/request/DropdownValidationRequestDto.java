package com.sougata.form_data_service.dto.validation.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DropdownValidationRequestDto extends ValidationRequest {

    private Integer responseIndex;

}
