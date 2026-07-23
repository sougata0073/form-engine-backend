package com.sougata.form_service.model.templateSchema;

import com.sougata.form_service.model.Form;
import com.sougata.form_service.model.questionSchema.LinearScale;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "linear_scale_templates")
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LinearScaleTemplate extends QuestionTemplate<LinearScaleTemplate> {

    @Column(nullable = false)
    private Integer fromNumber;

    @Column(nullable = false)
    private Integer toNumber;


    @Override
    public LinearScale fromTemplate(Form f, LinearScaleTemplate t) {
        var linearScale = new LinearScale();

//        linearScale.setQuestion(t.getQuestion());
//        linearScale.setDescription(t.getDescription());
//        linearScale.setRequired(t.getRequired());
//        linearScale.setForm(f);
//        linearScale.setOrderIndex(t.getOrderIndex());
//        linearScale.setFromNumber(t.getFromNumber());
//        linearScale.setToNumber(t.getToNumber());

        return linearScale;
    }

}
