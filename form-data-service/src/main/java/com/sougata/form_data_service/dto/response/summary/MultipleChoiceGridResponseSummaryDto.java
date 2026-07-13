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
public class MultipleChoiceGridResponseSummaryDto extends ResponseSummaryDto {

    private List<RowResponse> responses;

    public record RowResponse(
            @JsonSerialize(using = ToStringSerializer.class)
            Long rowId,

            String row,

            List<ColumnResponse> responses
    ) {

    }

    public record ColumnResponse(
            @JsonSerialize(using = ToStringSerializer.class)
            Long columnId,

            String column,

            @JsonSerialize(using = ToStringSerializer.class)
            Long responseCount
    ) {

    }

}
