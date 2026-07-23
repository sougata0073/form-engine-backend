package com.sougata.form_service.model.templateSchema;

import com.sougata.form_service.model.Form;
import com.sougata.form_service.model.questionSchema.Duration;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "duration_templates")
@EntityListeners(AuditingEntityListener.class)
public class DurationTemplate extends QuestionTemplate<DurationTemplate> {

    @Override
    public Duration fromTemplate(Form f, DurationTemplate t) {
        var duration = new Duration();

//        duration.setQuestion(t.getQuestion());
//        duration.setDescription(t.getDescription());
//        duration.setRequired(t.getRequired());
//        duration.setForm(f);
//        duration.setOrderIndex(t.getOrderIndex());

        return duration;
    }

}
