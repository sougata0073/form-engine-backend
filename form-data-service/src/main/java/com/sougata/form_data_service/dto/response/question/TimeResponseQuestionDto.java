package com.sougata.form_data_service.dto.response.question;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@NoArgsConstructor
@Getter
@Setter
public class TimeResponseQuestionDto extends ResponseQuestionDto<TimeResponseQuestionDto.Response> {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Response extends ResponseByQuestionResponse {
        private Instant time;
    }

    @NoArgsConstructor
    public static class Summary extends ResponseByQuestionSummary {
    }

}
