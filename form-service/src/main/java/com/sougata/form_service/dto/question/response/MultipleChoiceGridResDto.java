package com.sougata.form_service.dto.question.response;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.model.MultipleChoiceGrid;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class MultipleChoiceGridResDto extends QuestionRes {
    private List<String> rows;
    private List<String> columns;

    public MultipleChoiceGridResDto(Long id, String question, String description, Boolean required, Integer orderIndex, QuestionType questionType, List<String> rows, List<String> columns) {
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
                Arrays.asList(mcg.getRows()),
                Arrays.asList(mcg.getColumns())
        );
    }
}
