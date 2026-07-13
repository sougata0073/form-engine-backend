package com.sougata.form_service.model.templateSchema;

import com.github.f4b6a3.tsid.TsidCreator;
import com.sougata.form_service.model.Form;
import com.sougata.form_service.model.Template;
import com.sougata.form_service.model.questionSchema.Question;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
public abstract class QuestionTemplate<Temp extends QuestionTemplate<Temp>> {

    @Id
    private Long id = TsidCreator.getTsid().toLong();

    @Column(columnDefinition = "text")
    private String question;

    @Column(columnDefinition = "text")
    private String description;

    @Column(nullable = false)
    private Boolean required;

    @Column(nullable = false)
    private Integer orderIndex;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Template template;

    public abstract Question fromTemplate(Form f, Temp t);

}
