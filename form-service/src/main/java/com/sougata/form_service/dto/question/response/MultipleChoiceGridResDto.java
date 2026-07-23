package com.sougata.form_service.dto.question.response;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.model.questionSchema.MultipleChoiceGrid;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class MultipleChoiceGridResDto extends QuestionRes {
    private Boolean eachRowRequired;
    private List<MultipleChoiceGridRowResDto> rows;
    private List<MultipleChoiceGridColumnResDto> columns;

    public record MultipleChoiceGridRowResDto(
            @JsonSerialize(using = ToStringSerializer.class)
            Long id,
            String row,
            Integer orderIndex
    ) {
    }

    public record MultipleChoiceGridColumnResDto(
            @JsonSerialize(using = ToStringSerializer.class)
            Long id,
            String column,
            Integer orderIndex
    ) {
    }
}
