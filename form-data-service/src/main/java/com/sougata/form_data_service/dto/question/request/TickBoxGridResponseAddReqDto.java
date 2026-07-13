package com.sougata.form_data_service.dto.question.request;

import jakarta.validation.Valid;
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
    private List<@Valid Row> rows;

    public record Row(
            @NotNull
            Long rowId,

            @NotNull
            @Size(max = 20)
            List<@NotNull Long> responseColumnIds
    ) {

    }
}
