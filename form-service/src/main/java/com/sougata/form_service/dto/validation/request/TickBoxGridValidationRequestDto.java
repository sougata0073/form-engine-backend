package com.sougata.form_service.dto.validation.request;

import com.sougata.form_service.constant.ValidationRequestValidationMessages;
import jakarta.validation.Valid;
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
public class TickBoxGridValidationRequestDto extends ValidationRequest {

    @NotNull(message = ValidationRequestValidationMessages.TICK_BOX_GRID_ROWS_NOT_NULL)
    @Size(max = 20, message = ValidationRequestValidationMessages.TICK_BOX_GRID_ROW_LIST_INVALID_SIZE)
    private List<@Valid TickBoxGridRowValidationRequestDto> rows;

}
