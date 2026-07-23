package com.sougata.form_data_service.dto.response.question;

import com.sougata.form_data_service.dto.question.response.MultipleChoiceResDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class MultipleChoiceResponseQuestionDto extends ResponseQuestionDto {

    private List<MultipleChoiceResDto.MultipleChoiceOptionResDto> options;
    private List<Response> responses;

    public record Response(
            @JsonSerialize(using = ToStringSerializer.class)
            Long optionId,
            Integer responseCount,
            List<String> responseIds
    ) {

    }
}
