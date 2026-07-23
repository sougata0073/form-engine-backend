package com.sougata.form_service.model.templateSchema;

import com.sougata.form_service.model.Form;
import com.sougata.form_service.model.questionSchema.Time;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "time_templates")
@EntityListeners(AuditingEntityListener.class)
public class TimeTemplate extends QuestionTemplate<TimeTemplate> {

    @Override
    public Time fromTemplate(Form f, TimeTemplate t) {
        var time = new Time();

//        time.setQuestion(t.getQuestion());
//        time.setDescription(t.getDescription());
//        time.setRequired(t.getRequired());
//        time.setForm(f);
//        time.setOrderIndex(t.getOrderIndex());

        return time;
    }

}
