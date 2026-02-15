package com.sougata.form_service.dto.question.request;

import com.sougata.form_service.constant.ValidationMessages;
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
public class MultipleChoiceAddUpdateReqDto extends QuestionAddUpdateReq {

    @NotNull(message = ValidationMessages.OPTIONS_NOT_NULL)
    @Size(min = 1, max = 20, message = ValidationMessages.OPTIONS_COUNT_RANGE)
    private List<@Length(min = 1) String> options;

}
