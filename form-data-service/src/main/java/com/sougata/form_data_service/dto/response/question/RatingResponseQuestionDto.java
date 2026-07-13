package com.sougata.form_data_service.dto.response.question;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class RatingResponseQuestionDto extends ResponseQuestionDto {

    private List<Response> responses;

    public record Response(
            Integer rating,
            Integer responseCount,
            List<String> responseIds
    ) {

    }

}
