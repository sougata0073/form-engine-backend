package com.sougata.form_data_service.dto.form;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FormInfoResDto {
    private UUID id;
    private String name;
    private String title;
    private String description;
    private Boolean published;
    private Boolean acceptingResponse;
    private String notAcceptingResponseMessage;
    private Instant stopAcceptingResponseOn;
    private Integer stopAcceptingResponseAfterResponse;
    private Instant lastOpenedOn;
}
