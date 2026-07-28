package com.sougata.form_service.model.templateSchema;

import com.sougata.form_service.model.Form;
import com.sougata.form_service.model.questionSchema.MultipleChoice;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "multiple_choice_templates")
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MultipleChoiceTemplate extends QuestionTemplate<MultipleChoiceTemplate> {

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "multipleChoiceTemplate")
    private List<MultipleChoiceOptionTemplate> options = new ArrayList<>();

    @Override
    public MultipleChoice fromTemplate(Form f, MultipleChoiceTemplate t) {
        var multipleChoice = new MultipleChoice();

//        multipleChoice.setQuestion(t.getQuestion());
//        multipleChoice.setDescription(t.getDescription());
//        multipleChoice.setRequired(t.getRequired());
//        multipleChoice.setForm(f);
//        multipleChoice.setOrderIndex(t.getOrderIndex());
//
//        var options = t.getOptions()
//                .stream()
//                .map(op -> {
//                    var option = new MultipleChoiceOption();
//
//                    option.setOption(op.getOption());
//                    option.setOrderIndex(op.getOrderIndex());
//                    option.setMultipleChoice(multipleChoice);
//
//                    return option;
//                })
//                .toList();
//
//        multipleChoice.setOptions(options);

        return multipleChoice;
    }

}
