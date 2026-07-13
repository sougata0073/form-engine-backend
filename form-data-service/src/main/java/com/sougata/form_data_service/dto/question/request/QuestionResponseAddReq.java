package com.sougata.form_data_service.dto.question.request;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.sougata.form_data_service.constant.QuestionType;
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
        @JsonSubTypes.Type(value = CheckboxResponseAddReqDto.class, name = "CHECKBOX"),
        @JsonSubTypes.Type(value = DateResponseAddReqDto.class, name = "DATE"),
        @JsonSubTypes.Type(value = DateTimeResponseAddReqDto.class, name = "DATE_TIME"),
        @JsonSubTypes.Type(value = DropdownResponseAddReqDto.class, name = "DROPDOWN"),
        @JsonSubTypes.Type(value = DurationResponseAddReqDto.class, name = "DURATION"),
        @JsonSubTypes.Type(value = FileUploadResponseAddReqDto.class, name = "FILE_UPLOAD"),
        @JsonSubTypes.Type(value = LinearScaleResponseAddReqDto.class, name = "LINEAR_SCALE"),
        @JsonSubTypes.Type(value = MultipleChoiceResponseAddReqDto.class, name = "MULTIPLE_CHOICE"),
        @JsonSubTypes.Type(value = MultipleChoiceGridResponseAddReqDto.class, name = "MULTIPLE_CHOICE_GRID"),
        @JsonSubTypes.Type(value = ParagraphResponseAddReqDto.class, name = "PARAGRAPH"),
        @JsonSubTypes.Type(value = RatingResponseAddReqDto.class, name = "RATING"),
        @JsonSubTypes.Type(value = ShortAnswerResponseAddReqDto.class, name = "SHORT_ANSWER"),
        @JsonSubTypes.Type(value = TickBoxGridResponseAddReqDto.class, name = "TICK_BOX_GRID"),
        @JsonSubTypes.Type(value = TimeResponseAddReqDto.class, name = "TIME")
})
@Getter
@Setter
@NoArgsConstructor
public class QuestionResponseAddReq {

    @NotNull
    private Long questionId;

    @NotNull
    private QuestionType questionType;

}
