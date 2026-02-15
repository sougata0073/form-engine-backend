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
public class MultipleChoiceGridAddUpdateReqDto extends QuestionAddUpdateReq {

    @NotNull(message = ValidationMessages.EACH_ROW_REQUIRED_NOT_NULL)
    private Boolean eachRowRequired;

    @NotNull(message = ValidationMessages.ROWS_NOT_NULL)
    @Size(min = 1, max = 20, message = ValidationMessages.ROWS_COUNT_RANGE)
    private List<@Length(min = 1) String> rows;

    @NotNull(message = ValidationMessages.COLUMNS_NOT_NULL)
    @Size(min = 1, max = 20, message = ValidationMessages.COLUMNS_COUNT_RANGE)
    private List<@Length(min = 1) String> columns;

}
