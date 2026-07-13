package com.sougata.form_service.dto.question.request;

import com.sougata.form_service.constant.ValidationMessages;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class DropdownAddUpdateReqDto extends QuestionAddUpdateReq {

    @NotNull(message = ValidationMessages.OPTIONS_NOT_NULL)
    @Size(min = 1, max = 20, message = ValidationMessages.OPTIONS_COUNT_RANGE)
    private List<@Valid Option> options;

    public record Option(
            Long id,

            @Length(min = 1)
            String option
    ) {
    }
}
