package com.sougata.form_data_service.dto.response.question;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ResponseByQuestionResponse {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long responseCount;
    private List<String> responseIds;
}
