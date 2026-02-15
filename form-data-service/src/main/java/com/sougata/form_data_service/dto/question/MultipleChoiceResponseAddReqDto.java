package com.sougata.form_data_service.dto.question;

import jakarta.validation.constraints.Max;
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
public class MultipleChoiceResponseAddReqDto extends QuestionResponseAddReq {

    @NotNull
    @Min(value = 0)
    @Max(value = 19)
    private Integer responseIndex;

}
