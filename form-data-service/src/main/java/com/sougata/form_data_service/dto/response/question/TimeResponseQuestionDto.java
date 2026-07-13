package com.sougata.form_data_service.dto.response.question;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class TimeResponseQuestionDto extends ResponseQuestionDto {

    private List<Response> responses;

    public record Response(
            Instant time,
            Integer responseCount,
            List<String> responseIds
    ) {

    }

}
