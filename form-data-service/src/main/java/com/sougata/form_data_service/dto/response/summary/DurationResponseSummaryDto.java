package com.sougata.form_data_service.dto.response.summary;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DurationResponseSummaryDto extends ResponseSummaryDto {

    private List<Response> responses;

    public record Response(
            Integer hours,
            Integer minutes,
            Integer seconds
    ) {

    }
}
