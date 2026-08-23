package com.sougata.form_data_service.dto.response.question;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@NoArgsConstructor
@Getter
@Setter
public class DateTimeResponseQuestionDto extends ResponseQuestionDto<DateTimeResponseQuestionDto.Response> {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Response extends ResponseByQuestionResponse {
        private Instant dateTime;
    }

    @NoArgsConstructor
    public static class Summary extends ResponseByQuestionSummary {
    }

}
