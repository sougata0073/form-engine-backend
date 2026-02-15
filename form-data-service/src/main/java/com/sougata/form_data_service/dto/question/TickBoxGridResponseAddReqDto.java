package com.sougata.form_data_service.dto.question;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TickBoxGridResponseAddReqDto extends QuestionResponseAddReq {

    @NotNull
    @Size(max = 20)
    private List<List<@NotNull @Min(0) @Max(19) Integer>> rows;

}
