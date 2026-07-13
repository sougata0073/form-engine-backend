package com.sougata.form_data_service.model;

import com.github.f4b6a3.tsid.TsidCreator;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
        name = "form_responses",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "unique_form_id_user_id",
                        columnNames = {"form_id", "user_id"}
                )
        }
)
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
    private List<Checkbox> checkboxes = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "formResponse")
    private List<Date> dates = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "formResponse")
    private List<DateTime> dateTimes = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "formResponse")
    private List<Dropdown> dropdowns = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "formResponse")
    private List<Duration> durations = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "formResponse")
    private List<FileUpload> fileUploads = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "formResponse")
    private List<LinearScale> linearScales = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "formResponse")
    private List<MultipleChoice> multipleChoices = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "formResponse")
    private List<MultipleChoiceGrid> multipleChoiceGrids = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "formResponse")
    private List<Paragraph> paragraphs = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "formResponse")
    private List<Rating> ratings = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "formResponse")
    private List<ShortAnswer> shortAnswers = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "formResponse")
    private List<TickBoxGrid> tickBoxGrids = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "formResponse")
    private List<Time> times = new ArrayList<>();

}
