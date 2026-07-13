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
public class DropdownValidationRequestDto extends ValidationRequest {

    @NotNull(message = ValidationRequestValidationMessages.RESPONSE_INDEX_NOT_NULL)
    private Long responseOptionId;

}
