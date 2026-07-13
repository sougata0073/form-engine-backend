package com.sougata.form_service.model.questionSchema;

import com.github.f4b6a3.tsid.TsidCreator;
import com.sougata.form_service.model.Auditable;
import com.sougata.form_service.model.Form;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public abstract class Question extends Auditable {

    @Id
    private Long id = TsidCreator.getTsid().toLong();

    @Column(columnDefinition = "text")
    private String question;

    @Column(columnDefinition = "text")
    private String description;

    @Column(nullable = false)
    private Boolean required;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Form form;

    @Column(nullable = false)
    private Integer orderIndex;
}
