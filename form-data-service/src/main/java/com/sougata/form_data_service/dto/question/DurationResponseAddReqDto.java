package com.sougata.form_data_service.dto.question;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DurationResponseAddReqDto extends QuestionResponseAddReq {

    @NotNull
    @Min(value = 0)
    private Integer hours;

    @NotNull
    @Min(value = 0)
    private Integer minutes;

    @NotNull
    @Min(value = 0)
    private Integer seconds;

}
