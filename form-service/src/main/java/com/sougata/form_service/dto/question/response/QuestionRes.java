package com.sougata.form_service.dto.question.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.sougata.form_service.constant.QuestionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "questionType",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = CheckboxResDto.class, name = "CHECKBOX"),
        @JsonSubTypes.Type(value = DateResDto.class, name = "DATE"),
        @JsonSubTypes.Type(value = DateTimeResDto.class, name = "DATE_TIME"),
        @JsonSubTypes.Type(value = DropdownResDto.class, name = "DROPDOWN"),
        @JsonSubTypes.Type(value = DurationResDto.class, name = "DURATION"),
        @JsonSubTypes.Type(value = FileUploadResDto.class, name = "FILE_UPLOAD"),
        @JsonSubTypes.Type(value = LinearScaleResDto.class, name = "LINEAR_SCALE"),
        @JsonSubTypes.Type(value = MultipleChoiceResDto.class, name = "MULTIPLE_CHOICE"),
        @JsonSubTypes.Type(value = MultipleChoiceGridResDto.class, name = "MULTIPLE_CHOICE_GRID"),
        @JsonSubTypes.Type(value = ParagraphResDto.class, name = "PARAGRAPH"),
        @JsonSubTypes.Type(value = RatingResDto.class, name = "RATING"),
        @JsonSubTypes.Type(value = ShortAnswerResDto.class, name = "SHORT_ANSWER"),
        @JsonSubTypes.Type(value = TickBoxGridResDto.class, name = "TICK_BOX_GRID"),
        @JsonSubTypes.Type(value = TimeResDto.class, name = "TIME")
})
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class QuestionRes {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private String question;
    private String description;
    private Boolean required;
    private Integer orderIndex;
    private QuestionType questionType;

    @JsonProperty(value = "@class")
    private String cls = getClass().getName();
}
