package com.sougata.form_service.projection;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.question.response.DateTimeResDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DateTimeProjection implements QuestionProjection<DateTimeResDto> {
    private Long id;
    private String question;
    private String description;
    private Boolean required;
    private Integer orderIndex;

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.DATE_TIME;
    }

    @Override
    public DateTimeResDto getQuestionResponse() {
        return new DateTimeResDto(
                id,
                question,
                description,
                required,
                orderIndex,
                QuestionType.DATE_TIME
        );
    }
}
