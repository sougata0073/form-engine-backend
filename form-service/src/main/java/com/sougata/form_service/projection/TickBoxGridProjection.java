package com.sougata.form_service.projection;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.question.response.TickBoxGridResDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Arrays;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TickBoxGridProjection implements QuestionProjection<TickBoxGridResDto> {
    private Long id;
    private String question;
    private String description;
    private Boolean required;
    private String[] rows;
    private String[] columns;
    private Integer orderIndex;

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.TICK_BOX_GRID;
    }

    @Override
    public TickBoxGridResDto getQuestionResponse() {
        return new TickBoxGridResDto(
                id,
                question,
                description,
                required,
                orderIndex,
                QuestionType.TICK_BOX_GRID,
                Arrays.asList(rows),
                Arrays.asList(columns)
        );
    }
}
