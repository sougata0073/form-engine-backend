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

    public record DropdownOptionResDto(
            @JsonSerialize(using = ToStringSerializer.class)
            Long id,
            String option,
            Integer orderIndex
    ) {
    }

}
