package com.sougata.form_data_service.dto.response.summary;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TimeResponseSummaryDto extends ResponseSummaryDto {

    private List<Instant> responses;

}
