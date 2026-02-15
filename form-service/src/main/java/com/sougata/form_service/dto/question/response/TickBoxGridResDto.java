package com.sougata.form_service.dto.question.response;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.model.TickBoxGrid;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class TickBoxGridResDto extends QuestionRes {
    private List<String> rows;
    private List<String> columns;

    public TickBoxGridResDto(Long id, String question, String description, Boolean required, Integer orderIndex, QuestionType questionType, List<String> rows, List<String> columns) {
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
                Arrays.asList(tbg.getRows()),
                Arrays.asList(tbg.getColumns())
        );
    }
}
