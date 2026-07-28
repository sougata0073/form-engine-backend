package com.sougata.form_service.model.templateSchema;

import com.sougata.form_service.model.Form;
import com.sougata.form_service.model.questionSchema.Dropdown;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "dropdown_templates")
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DropdownTemplate extends QuestionTemplate<DropdownTemplate> {

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "dropdownTemplate")
    private List<DropdownOptionTemplate> options = new ArrayList<>();

    @Override
    public Dropdown fromTemplate(Form f, DropdownTemplate t) {
        var dropdown = new Dropdown();

//        dropdown.setQuestion(t.getQuestion());
//        dropdown.setDescription(t.getDescription());
//        dropdown.setRequired(t.getRequired());
//        dropdown.setForm(f);
//        dropdown.setOrderIndex(t.getOrderIndex());
//
//        var options = t.getOptions()
//                .stream()
//                .map(op -> {
//                    var option = new DropdownOption();
//
//                    option.setOption(op.getOption());
//                    option.setOrderIndex(op.getOrderIndex());
//                    option.setDropdown(dropdown);
//
//                    return option;
//                })
//                .toList();
//
//        dropdown.setOptions(options);

        return dropdown;
    }

}
