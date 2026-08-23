package com.sougata.form_data_service.dto.response.question;

import com.sougata.form_data_service.dto.question.response.CheckboxDetailsDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class CheckboxResponseQuestionDto extends ResponseQuestionDto<CheckboxResponseQuestionDto.Response> {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Response extends ResponseByQuestionResponse {
        private List<String> optionIds;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Summary extends ResponseByQuestionSummary {
        private List<CheckboxDetailsDto.CheckboxOptionResDto> options;
    }

}
