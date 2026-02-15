package com.sougata.form_data_service.dto.question;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CheckboxResponseAddReqDto extends QuestionResponseAddReq {

    @NotNull
    @Size(max = 20)
    private List<@Min(value = 0) @Max(value = 19) Integer> responseIndexes;

}
