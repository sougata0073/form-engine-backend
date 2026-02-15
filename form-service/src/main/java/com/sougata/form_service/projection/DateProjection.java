package com.sougata.form_service.projection;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.question.response.DateResDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DateProjection implements QuestionProjection<DateResDto> {
    private Long id;
    private String question;
    private String description;
    private Boolean required;
    private Integer orderIndex;

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.DATE;
    }

    @Override
    public DateResDto getQuestionResponse() {
        return new DateResDto(
                id,
                question,
                description,
                required,
                orderIndex,
                QuestionType.DATE
        );
    }
}
