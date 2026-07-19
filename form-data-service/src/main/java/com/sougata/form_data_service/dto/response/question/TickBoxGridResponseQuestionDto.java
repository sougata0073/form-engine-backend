package com.sougata.form_data_service.dto.response.question;

import com.sougata.form_data_service.form_schema.dto.questionSchema.response.TickBoxGridResDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class TickBoxGridResponseQuestionDto extends ResponseQuestionDto {

    private List<TickBoxGridResDto.TickBoxGridRowResDto> rows;
    private List<TickBoxGridResDto.TickBoxGridColumnResDto> columns;
    private List<RowRes> responses;

    public record RowRes(
            @JsonSerialize(using = ToStringSerializer.class)
            Long rowId,
            List<ColumnRes> responses
    ) {
    }

    public record ColumnRes(
            List<String> columnIds,
            Integer responseCount,
            List<String> responseIds
    ) {
    }

}
