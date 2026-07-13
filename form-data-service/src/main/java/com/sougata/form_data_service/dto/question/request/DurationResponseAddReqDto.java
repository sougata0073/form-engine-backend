package com.sougata.form_data_service.dto.question.request;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DurationResponseAddReqDto extends QuestionResponseAddReq {

    @Min(value = 0)
    private Integer hours;

    @Min(value = 0)
    private Integer minutes;

    @Min(value = 0)
    private Integer seconds;

}
