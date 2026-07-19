package com.sougata.form_data_service.dto.response.question;

import com.sougata.form_data_service.form_schema.dto.questionSchema.response.CheckboxResDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class CheckboxResponseQuestionDto extends ResponseQuestionDto {

    private List<CheckboxResDto.CheckboxOptionResDto> options;
    private List<Response> responses;

    public record Response(
            List<String> optionIds,
            Integer responseCount,
            List<String> responseIds
    ) {

    }

}
