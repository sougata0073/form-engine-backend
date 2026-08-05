package com.sougata.form_data_service.dto.response.question;

import com.sougata.form_data_service.dto.question.response.MultipleChoiceResDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class MultipleChoiceResponseQuestionDto extends ResponseQuestionDto<MultipleChoiceResponseQuestionDto.Response> {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Response extends ResponseByQuestionResponse {

        @JsonSerialize(using = ToStringSerializer.class)
        private Long optionId;

    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Summary extends ResponseByQuestionSummary {
        private List<MultipleChoiceResDto.MultipleChoiceOptionResDto> options;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FormResponsesReqDto {
        private String optionId;
    }
}
