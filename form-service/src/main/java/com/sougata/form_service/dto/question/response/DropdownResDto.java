package com.sougata.form_service.dto.question.response;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.model.Dropdown;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class DropdownResDto extends QuestionRes {
    private List<String> options;

    public DropdownResDto(Long id, String question, String description, Boolean required, List<String> options, Integer orderIndex, QuestionType questionType) {
        super(id, question, description, required, orderIndex, questionType);
        this.options = options;
    }

    public static DropdownResDto create(Dropdown dropdown) {
        return new DropdownResDto(
                dropdown.getId(),
                dropdown.getQuestion(),
                dropdown.getDescription(),
                dropdown.getRequired(),
                Arrays.asList(dropdown.getOptions()),
                dropdown.getOrderIndex(),
                QuestionType.DROPDOWN
        );
    }

}
