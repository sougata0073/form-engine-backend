package com.sougata.form_service.dto.question.response;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.model.questionSchema.MultipleChoice;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class MultipleChoiceResDto extends QuestionRes {
    private List<MultipleChoiceOptionResDto> options;

    public MultipleChoiceResDto(Long id, String question, String description, Boolean required, Integer orderIndex, QuestionType questionType, List<MultipleChoiceOptionResDto> options) {
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
                mc.getOptions().stream().map(op -> new MultipleChoiceOptionResDto(op.getId(), op.getOption(), op.getOrderIndex())).toList()
        );
    }

    public record MultipleChoiceOptionResDto(
            @JsonSerialize(using = ToStringSerializer.class)
            Long id,
            String option,
            Integer orderIndex
    ) {
    }

}
