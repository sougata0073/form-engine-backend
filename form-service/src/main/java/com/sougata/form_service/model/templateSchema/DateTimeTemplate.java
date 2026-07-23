package com.sougata.form_service.model.templateSchema;

import com.sougata.form_service.model.Form;
import com.sougata.form_service.model.questionSchema.DateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "date_time_templates")
@EntityListeners(AuditingEntityListener.class)
public class DateTimeTemplate extends QuestionTemplate<DateTimeTemplate> {

    @Override
    public DateTime fromTemplate(Form f, DateTimeTemplate t) {
        var dateTime = new DateTime();

//        dateTime.setQuestion(t.getQuestion());
//        dateTime.setDescription(t.getDescription());
//        dateTime.setRequired(t.getRequired());
//        dateTime.setForm(f);
//        dateTime.setOrderIndex(t.getOrderIndex());

        return dateTime;
    }

}
