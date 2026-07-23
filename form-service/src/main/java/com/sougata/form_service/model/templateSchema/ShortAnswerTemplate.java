package com.sougata.form_service.model.templateSchema;

import com.fasterxml.jackson.databind.JsonNode;
import com.sougata.form_service.model.Form;
import com.sougata.form_service.model.questionSchema.ShortAnswer;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "short_answer_templates")
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ShortAnswerTemplate extends QuestionTemplate<ShortAnswerTemplate> {

    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private JsonNode validationConfig;

    @Override
    public ShortAnswer fromTemplate(Form f, ShortAnswerTemplate t) {
        var shortAnswer = new ShortAnswer();

//        shortAnswer.setQuestion(t.getQuestion());
//        shortAnswer.setDescription(t.getDescription());
//        shortAnswer.setRequired(t.getRequired());
//        shortAnswer.setForm(f);
//        shortAnswer.setOrderIndex(t.getOrderIndex());
//        shortAnswer.setValidationConfig(t.getValidationConfig());

        return shortAnswer;
    }
}
