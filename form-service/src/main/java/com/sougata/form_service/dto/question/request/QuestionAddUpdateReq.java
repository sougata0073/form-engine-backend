package com.sougata.form_service.dto.question.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.constant.ValidationMessages;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "questionType",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = CheckboxAddUpdateReqDto.class, name = "CHECKBOX"),
        @JsonSubTypes.Type(value = DateAddUpdateReqDto.class, name = "DATE"),
        @JsonSubTypes.Type(value = DateTimeAddUpdateReqDto.class, name = "DATE_TIME"),
        @JsonSubTypes.Type(value = DropdownAddUpdateReqDto.class, name = "DROPDOWN"),
        @JsonSubTypes.Type(value = DurationAddUpdateReqDto.class, name = "DURATION"),
        @JsonSubTypes.Type(value = FileUploadAddUpdateReqDto.class, name = "FILE_UPLOAD"),
        @JsonSubTypes.Type(value = LinearScaleAddUpdateReqDto.class, name = "LINEAR_SCALE"),
        @JsonSubTypes.Type(value = MultipleChoiceAddUpdateReqDto.class, name = "MULTIPLE_CHOICE"),
        @JsonSubTypes.Type(value = MultipleChoiceGridAddUpdateReqDto.class, name = "MULTIPLE_CHOICE_GRID"),
        @JsonSubTypes.Type(value = ParagraphAddUpdateReqDto.class, name = "PARAGRAPH"),
        @JsonSubTypes.Type(value = RatingAddUpdateReqDto.class, name = "RATING"),
        @JsonSubTypes.Type(value = ShortAnswerAddUpdateReqDto.class, name = "SHORT_ANSWER"),
        @JsonSubTypes.Type(value = TickBoxGridAddUpdateReqDto.class, name = "TICK_BOX_GRID"),
        @JsonSubTypes.Type(value = TimeAddUpdateReqDto.class, name = "TIME")
})
@NoArgsConstructor
@Getter
@Setter
public class QuestionAddUpdateReq {

    private String question;

    private String description;

    @NotNull(message = ValidationMessages.REQUIRED_NOT_NULL)
    private Boolean required;

    @NotNull(message = ValidationMessages.QUESTION_TYPE_NOT_NULL)
    private QuestionType questionType;

    @JsonProperty(value = "@class")
    private String cls = getClass().getName();

}
