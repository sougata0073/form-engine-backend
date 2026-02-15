package com.sougata.form_service.dto.validation.request;

import com.sougata.form_service.constant.ValidationRequestValidationMessages;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CheckboxValidationRequestDto extends ValidationRequest {

    @NotNull(message = ValidationRequestValidationMessages.CHECKBOX_RESPONSE_INDEX_LIST_NOT_NULL)
    @Size(max = 20, message = ValidationRequestValidationMessages.CHECKBOX_RESPONSE_INDEX_LIST_INVALID_SIZE)
    private List<
            @NotNull(message = ValidationRequestValidationMessages.RESPONSE_INDEX_NOT_NULL)
            @Min(value = 0, message = ValidationRequestValidationMessages.RESPONSE_INDEX_INVALID_RANGE)
            @Max(value = 19, message = ValidationRequestValidationMessages.RESPONSE_INDEX_INVALID_RANGE)
                    Integer> responseIndexes;

}
