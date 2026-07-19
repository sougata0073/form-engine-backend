package com.sougata.form_data_service.dto.response.question;

import com.sougata.form_data_service.form_schema.dto.questionSchema.response.MultipleChoiceGridResDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class MultipleChoiceGridResponseQuestionDto extends ResponseQuestionDto {

    private List<MultipleChoiceGridResDto.MultipleChoiceGridRowResDto> rows;
    private List<MultipleChoiceGridResDto.MultipleChoiceGridColumnResDto> columns;
    private List<RowRes> responses;

    public record RowRes(
            @JsonSerialize(using = ToStringSerializer.class)
            Long rowId,
            List<ColumnRes> responses
    ) {
    }

    public record ColumnRes(
            @JsonSerialize(using = ToStringSerializer.class)
            Long columnId,
            Integer responseCount,
            List<String> responseIds
    ) {
    }

}
