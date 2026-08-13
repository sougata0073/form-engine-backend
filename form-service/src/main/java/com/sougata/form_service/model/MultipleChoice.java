package com.sougata.form_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "multiple_choices")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MultipleChoice extends AnyTypeQuestion {

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "multipleChoice")
    private List<MultipleChoiceOption> options = new ArrayList<>();

}
