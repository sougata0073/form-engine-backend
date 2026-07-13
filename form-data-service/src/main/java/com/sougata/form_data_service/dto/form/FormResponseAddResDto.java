package com.sougata.form_data_service.dto.form;

import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

public record FormResponseAddResDto(

        @JsonSerialize(using = ToStringSerializer.class)
        Long id

) {

}
