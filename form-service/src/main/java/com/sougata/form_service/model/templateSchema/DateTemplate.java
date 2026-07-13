package com.sougata.form_service.model.templateSchema;

import com.sougata.form_service.model.Form;
import com.sougata.form_service.model.questionSchema.Date;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "date_templates")
@EntityListeners(AuditingEntityListener.class)
public class DateTemplate extends QuestionTemplate<DateTemplate> {

    @Override
    public Date fromTemplate(Form f, DateTemplate t) {
        var date = new Date();

        date.setQuestion(t.getQuestion());
        date.setDescription(t.getDescription());
        date.setRequired(t.getRequired());
        date.setForm(f);
        date.setOrderIndex(t.getOrderIndex());

        return date;
    }

}
