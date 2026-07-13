package com.sougata.form_service.dto.question.response;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.model.questionSchema.Time;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class TimeResDto extends QuestionRes {

    public TimeResDto(Long id, String question, String description, Boolean required, Integer orderIndex, QuestionType questionType) {
        super(id, question, description, required, orderIndex, questionType);
    }

    public static TimeResDto create(Time time) {
        return new TimeResDto(
                time.getId(),
                time.getQuestion(),
                time.getDescription(),
                time.getRequired(),
                time.getOrderIndex(),
                QuestionType.TIME
        );
    }
}
