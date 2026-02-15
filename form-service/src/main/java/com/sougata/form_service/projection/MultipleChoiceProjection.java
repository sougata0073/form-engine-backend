package com.sougata.form_service.projection;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.question.response.MultipleChoiceResDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Arrays;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MultipleChoiceProjection implements QuestionProjection<MultipleChoiceResDto> {
    private Long id;
    private String question;
    private String description;
    private Boolean required;
    private String[] options;
    private Integer orderIndex;

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.MULTIPLE_CHOICE;
    }

    @Override
    public MultipleChoiceResDto getQuestionResponse() {
        return new MultipleChoiceResDto(
                id,
                question,
                description,
                required,
                orderIndex,
                QuestionType.MULTIPLE_CHOICE,
                Arrays.asList(options)
        );
    }
}
