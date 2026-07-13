package com.sougata.form_data_service.dto.response.question;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class DateResponseQuestionDto extends ResponseQuestionDto {

    private List<Response> responses;

    public record Response(
            Instant date,
            Integer responseCount,
            List<String> responseIds
    ) {

    }

}
