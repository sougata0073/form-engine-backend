package com.sougata.form_service.projection;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.question.response.MultipleChoiceGridResDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Arrays;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MultipleChoiceGridProjection implements QuestionProjection<MultipleChoiceGridResDto> {
    private Long id;
    private String question;
    private String description;
    private Boolean required;
    private String[] rows;
    private String[] columns;
    private Integer orderIndex;

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.MULTIPLE_CHOICE_GRID;
    }

    @Override
    public MultipleChoiceGridResDto getQuestionResponse() {
        return new MultipleChoiceGridResDto(
                id,
                question,
                description,
                required,
                orderIndex,
                QuestionType.MULTIPLE_CHOICE_GRID,
                Arrays.asList(rows),
                Arrays.asList(columns)
        );
    }
}
