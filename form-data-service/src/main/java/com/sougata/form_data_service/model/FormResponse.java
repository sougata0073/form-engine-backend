package com.sougata.form_data_service.model;

import com.github.f4b6a3.tsid.TsidCreator;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "form_responses")
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FormResponse extends Auditable {

    @Id
    private Long id = TsidCreator.getTsid().toLong();

    @Column(nullable = false)
    private UUID formId;

    @Column(nullable = false)
    private UUID userId;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "formResponse")
    private List<Checkbox> checkboxes;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "formResponse")
    private List<Date> dates;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "formResponse")
    private List<DateTime> dateTimes;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "formResponse")
    private List<Dropdown> dropdowns;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "formResponse")
    private List<Duration> durations;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "formResponse")
    private List<FileUpload> fileUploads;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "formResponse")
    private List<LinearScale> linearScales;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "formResponse")
    private List<MultipleChoice> multipleChoices;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "formResponse")
    private List<MultipleChoiceGrid> multipleChoiceGrids;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "formResponse")
    private List<Paragraph> paragraphs;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "formResponse")
    private List<Rating> ratings;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "formResponse")
    private List<ShortAnswer> shortAnswers;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "formResponse")
    private List<TickBoxGrid> tickBoxGrids;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "formResponse")
    private List<Time> times;

}
