package com.sougata.form_service.dto.question.response;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.model.questionSchema.TickBoxGrid;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class TickBoxGridResDto extends QuestionRes {
    private List<TickBoxGridRowResDto> rows;
    private List<TickBoxGridColumnResDto> columns;

    public TickBoxGridResDto(Long id, String question, String description, Boolean required, Integer orderIndex, QuestionType questionType, List<TickBoxGridRowResDto> rows, List<TickBoxGridColumnResDto> columns) {
        super(id, question, description, required, orderIndex, questionType);
        this.rows = rows;
        this.columns = columns;
    }

    public static TickBoxGridResDto create(TickBoxGrid tbg) {
        return new TickBoxGridResDto(
                tbg.getId(),
                tbg.getQuestion(),
                tbg.getDescription(),
                tbg.getRequired(),
                tbg.getOrderIndex(),
                QuestionType.TICK_BOX_GRID,
                tbg.getRows().stream().map(row -> new TickBoxGridResDto.TickBoxGridRowResDto(row.getId(), row.getRowName(), row.getOrderIndex())).toList(),
                tbg.getColumns().stream().map(column -> new TickBoxGridResDto.TickBoxGridColumnResDto(column.getId(), column.getColumnName(), column.getOrderIndex())).toList()
        );
    }

    public record TickBoxGridRowResDto(
            @JsonSerialize(using = ToStringSerializer.class)
            Long id,
            String row,
            Integer orderIndex
    ) {
    }

    public record TickBoxGridColumnResDto(
            @JsonSerialize(using = ToStringSerializer.class)
            Long id,
            String column,
            Integer orderIndex
    ) {
    }
}
