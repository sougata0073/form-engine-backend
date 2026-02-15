package com.sougata.form_service.projection;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.question.response.TimeResDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TimeProjection implements QuestionProjection<TimeResDto> {
    private Long id;
    private String question;
    private String description;
    private Boolean required;
    private Integer orderIndex;

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.TIME;
    }

    @Override
    public TimeResDto getQuestionResponse() {
        return new TimeResDto(
                id,
                question,
                description,
                required,
                orderIndex,
                QuestionType.TIME
        );
    }
}
