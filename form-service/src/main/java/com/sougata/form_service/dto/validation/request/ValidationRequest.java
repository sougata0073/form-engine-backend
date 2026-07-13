package com.sougata.form_service.dto.validation.request;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.constant.ValidationRequestValidationMessages;
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
        @JsonSubTypes.Type(value = CheckboxValidationRequestDto.class, name = "CHECKBOX"),
        @JsonSubTypes.Type(value = DateValidationRequestDto.class, name = "DATE"),
        @JsonSubTypes.Type(value = DateTimeValidationRequestDto.class, name = "DATE_TIME"),
        @JsonSubTypes.Type(value = DropdownValidationRequestDto.class, name = "DROPDOWN"),
        @JsonSubTypes.Type(value = DurationValidationRequestDto.class, name = "DURATION"),
        @JsonSubTypes.Type(value = FileUploadValidationRequestDto.class, name = "FILE_UPLOAD"),
        @JsonSubTypes.Type(value = LinearScaleValidationRequestDto.class, name = "LINEAR_SCALE"),
        @JsonSubTypes.Type(value = MultipleChoiceValidationRequestDto.class, name = "MULTIPLE_CHOICE"),
        @JsonSubTypes.Type(value = MultipleChoiceGridValidationRequestDto.class, name = "MULTIPLE_CHOICE_GRID"),
        @JsonSubTypes.Type(value = ParagraphValidationRequestDto.class, name = "PARAGRAPH"),
        @JsonSubTypes.Type(value = RatingValidationRequestDto.class, name = "RATING"),
        @JsonSubTypes.Type(value = ShortAnswerValidationRequestDto.class, name = "SHORT_ANSWER"),
        @JsonSubTypes.Type(value = TickBoxGridValidationRequestDto.class, name = "TICK_BOX_GRID"),
        @JsonSubTypes.Type(value = TimeValidationRequestDto.class, name = "TIME")
})
@NoArgsConstructor
@Getter
@Setter
public class ValidationRequest {

    @NotNull(message = ValidationRequestValidationMessages.QUESTION_ID_NOT_NULL)
    private Long questionId;

    @NotNull(message = ValidationRequestValidationMessages.QUESTION_TYPE_NOT_NULL)
    private QuestionType questionType;

}
