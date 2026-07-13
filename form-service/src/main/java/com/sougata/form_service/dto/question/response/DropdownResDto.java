package com.sougata.form_service.dto.question.response;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.model.questionSchema.Dropdown;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class DropdownResDto extends QuestionRes {
    private List<DropdownOptionResDto> options;

    public DropdownResDto(Long id, String question, String description, Boolean required, List<DropdownOptionResDto> options, Integer orderIndex, QuestionType questionType) {
        super(id, question, description, required, orderIndex, questionType);
        this.options = options;
    }

    public static DropdownResDto create(Dropdown dropdown) {
        return new DropdownResDto(
                dropdown.getId(),
                dropdown.getQuestion(),
                dropdown.getDescription(),
                dropdown.getRequired(),
                dropdown.getOptions().stream().map(o -> new DropdownOptionResDto(o.getId(), o.getOption(), o.getOrderIndex())).toList(),
                dropdown.getOrderIndex(),
                QuestionType.DROPDOWN
        );
    }

    public record DropdownOptionResDto(
            @JsonSerialize(using = ToStringSerializer.class)
            Long id,
            String option,
            Integer orderIndex
    ) {
    }

}
