package com.sougata.form_data_service.dto.question;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record TickBoxGridRowResponseAddReqDto(

        @NotNull
        @Min(value = 0)
        @Max(value = 19)
        Integer rowIndex,

        @NotNull
        @Size(max = 20)
        List<@NotNull @Min(value = 0) @Max(value = 19) Integer> responseIndexes
) {
}
