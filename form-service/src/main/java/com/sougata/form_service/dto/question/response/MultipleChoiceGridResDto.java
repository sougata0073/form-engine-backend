package com.sougata.form_service.dto.question.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class MultipleChoiceGridResDto extends QuestionRes {
    private Boolean eachRowRequired;
    private List<MultipleChoiceGridRowResDto> rows;
    private List<MultipleChoiceGridColumnResDto> columns;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MultipleChoiceGridRowResDto {
        @JsonSerialize(using = ToStringSerializer.class)
        private Long id;
        private String row;
        private Integer orderIndex;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MultipleChoiceGridColumnResDto {
        @JsonSerialize(using = ToStringSerializer.class)
        private Long id;
        private String column;
        private Integer orderIndex;
    }
}
