package com.sougata.form_service.model.templateSchema;

import com.fasterxml.jackson.databind.JsonNode;
import com.sougata.form_service.model.Form;
import com.sougata.form_service.model.questionSchema.Checkbox;
import com.sougata.form_service.model.questionSchema.CheckboxOption;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "checkbox_templates")
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CheckboxTemplate extends QuestionTemplate<CheckboxTemplate> {

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "checkboxTemplate")
    private List<CheckboxOptionTemplate> options = new ArrayList<>();

    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private JsonNode validationConfig;

    @Override
    public Checkbox fromTemplate(Form f, CheckboxTemplate t) {
        var checkbox = new Checkbox();

        checkbox.setQuestion(t.getQuestion());
        checkbox.setDescription(t.getDescription());
        checkbox.setRequired(t.getRequired());
        checkbox.setForm(f);
        checkbox.setOrderIndex(t.getOrderIndex());
        checkbox.setValidationConfig(t.getValidationConfig());

        var options = t.getOptions()
                .stream()
                .map(op -> {
                    var option = new CheckboxOption();

                    option.setOption(op.getOption());
                    option.setOrderIndex(op.getOrderIndex());
                    option.setCheckbox(checkbox);

                    return option;
                })
                .toList();

        checkbox.setOptions(options);

        return checkbox;
    }
}
