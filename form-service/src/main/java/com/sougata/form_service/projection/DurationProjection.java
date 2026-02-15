package com.sougata.form_service.projection;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.question.response.DurationResDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DurationProjection implements QuestionProjection<DurationResDto> {
    private Long id;
    private String question;
    private String description;
    private Boolean required;
    private Integer orderIndex;

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.DURATION;
    }

    @Override
    public DurationResDto getQuestionResponse() {
        return new DurationResDto(
                id,
                question,
                description,
                required,
                orderIndex,
                QuestionType.DURATION
        );
    }
}
