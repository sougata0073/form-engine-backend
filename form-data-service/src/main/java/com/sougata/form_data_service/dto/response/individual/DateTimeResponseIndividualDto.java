package com.sougata.form_data_service.dto.response.individual;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DateTimeResponseIndividualDto extends ResponseIndividualDto {
    private Instant dateTime;
}
