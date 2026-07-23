package com.sougata.form_service.model.templateSchema;

import com.fasterxml.jackson.databind.JsonNode;
import com.sougata.form_service.model.Form;
import com.sougata.form_service.model.questionSchema.Paragraph;
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
@Table(name = "paragraph_templates")
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ParagraphTemplate extends QuestionTemplate<ParagraphTemplate> {

    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private JsonNode validationConfig;

    @Override
    public Paragraph fromTemplate(Form f, ParagraphTemplate t) {
        var paragraph = new Paragraph();

//        paragraph.setQuestion(t.getQuestion());
//        paragraph.setDescription(t.getDescription());
//        paragraph.setRequired(t.getRequired());
//        paragraph.setForm(f);
//        paragraph.setOrderIndex(t.getOrderIndex());
//        paragraph.setValidationConfig(t.getValidationConfig());

        return paragraph;
    }
}
