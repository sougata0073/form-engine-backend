package com.sougata.form_service.dto.question.response;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.model.MultipleChoice;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class MultipleChoiceResDto extends QuestionRes {
    private List<String> options;

    public MultipleChoiceResDto(Long id, String question, String description, Boolean required, Integer orderIndex, QuestionType questionType, List<String> options) {
        super(id, question, description, required, orderIndex, questionType);
        this.options = options;
    }

    public static MultipleChoiceResDto create(MultipleChoice mc) {
        return new MultipleChoiceResDto(
                mc.getId(),
                mc.getQuestion(),
                mc.getDescription(),
                mc.getRequired(),
                mc.getOrderIndex(),
                QuestionType.MULTIPLE_CHOICE,
                Arrays.asList(mc.getOptions())
        );
    }

}
