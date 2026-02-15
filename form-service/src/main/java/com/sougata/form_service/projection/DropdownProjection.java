package com.sougata.form_service.projection;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.question.response.DropdownResDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Arrays;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DropdownProjection implements QuestionProjection<DropdownResDto> {
    private Long id;
    private String question;
    private String description;
    private Boolean required;
    private String[] options;
    private Integer orderIndex;

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.DROPDOWN;
    }

    @Override
    public DropdownResDto getQuestionResponse() {
        return new DropdownResDto(
                id,
                question,
                description,
                required,
                Arrays.asList(options),
                orderIndex,
                QuestionType.DROPDOWN
        );
    }
}
