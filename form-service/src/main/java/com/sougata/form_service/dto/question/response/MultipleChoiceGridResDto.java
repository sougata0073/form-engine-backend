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
    private List<MultipleChoiceGridRowResDto> rows;
    private List<MultipleChoiceGridColumnResDto> columns;

    public MultipleChoiceGridResDto(Long id, String question, String description, Boolean required, Integer orderIndex, QuestionType questionType, List<MultipleChoiceGridRowResDto> rows, List<MultipleChoiceGridColumnResDto> columns) {
        super(id, question, description, required, orderIndex, questionType);
        this.rows = rows;
        this.columns = columns;
    }

    public static MultipleChoiceGridResDto create(MultipleChoiceGrid mcg) {
        return new MultipleChoiceGridResDto(
                mcg.getId(),
                mcg.getQuestion(),
                mcg.getDescription(),
                mcg.getRequired(),
                mcg.getOrderIndex(),
                QuestionType.MULTIPLE_CHOICE_GRID,
                mcg.getRows().stream().map(row -> new MultipleChoiceGridRowResDto(row.getId(), row.getRowName(), row.getOrderIndex())).toList(),
                mcg.getColumns().stream().map(column -> new MultipleChoiceGridColumnResDto(column.getId(), column.getColumnName(), column.getOrderIndex())).toList()
        );
    }

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
