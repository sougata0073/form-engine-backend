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
public class LinearScaleResponseSummaryDto extends ResponseSummaryDto {

    private List<Response> responses;

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class Response {
        private Integer scale;

        @JsonSerialize(using = ToStringSerializer.class)
        private Long responseCount;
    }

}
