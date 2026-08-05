package com.sougata.form_data_service.dto.form;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FormResponseSummaryDto {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long responseId;
    private UUID responderId;
    private String responderUserName;
    private String responderEmail;
    private String responderAvatarUrl;
}
