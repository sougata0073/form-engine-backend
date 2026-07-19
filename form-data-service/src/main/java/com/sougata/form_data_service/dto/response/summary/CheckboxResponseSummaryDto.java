package com.sougata.form_data_service.dto.response.summary;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CheckboxResponseSummaryDto extends ResponseSummaryDto {

    private List<Response> responses;

    public record Response(
            @JsonSerialize(using = ToStringSerializer.class)
            Long optionId,

            String option,

            @JsonSerialize(using = ToStringSerializer.class)
            Long responseCount
    ) {

    }

}
