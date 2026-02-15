package com.sougata.form_service.projection;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.question.response.LinearScaleResDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LinearScaleProjection implements QuestionProjection<LinearScaleResDto> {
    private Long id;
    private String question;
    private String description;
    private Boolean required;
    private Integer fromNumber;
    private Integer toNumber;
    private Integer orderIndex;

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.LINEAR_SCALE;
    }

    @Override
    public LinearScaleResDto getQuestionResponse() {
        return new LinearScaleResDto(
                id,
                question,
                description,
                required,
                orderIndex,
                QuestionType.LINEAR_SCALE,
                fromNumber,
                toNumber
        );
    }
}
