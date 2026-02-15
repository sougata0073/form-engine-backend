package com.sougata.form_service.dto.question.response;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.model.LinearScale;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class LinearScaleResDto extends QuestionRes {
    private Integer fromNumber;
    private Integer toNumber;

    public LinearScaleResDto(Long id, String question, String description, Boolean required, Integer orderIndex, QuestionType questionType, Integer fromNumber, Integer toNumber) {
        super(id, question, description, required, orderIndex, questionType);
        this.fromNumber = fromNumber;
        this.toNumber = toNumber;
    }

    public static LinearScaleResDto create(LinearScale linearScale) {
        return new LinearScaleResDto(
                linearScale.getId(),
                linearScale.getQuestion(),
                linearScale.getDescription(),
                linearScale.getRequired(),
                linearScale.getOrderIndex(),
                QuestionType.LINEAR_SCALE,
                linearScale.getFromNumber(),
                linearScale.getToNumber()
        );
    }

}
