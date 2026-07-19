package com.sougata.form_service.model;

import com.sougata.form_service.model.questionSchema.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "forms")
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Form extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Column(columnDefinition = "text")
    private String name;

    @Column(columnDefinition = "text")
    private String title;

    @Column(columnDefinition = "text")
    private String description;

    @Column(nullable = false)
    private Boolean published;

    @Column(nullable = false)
    private Boolean acceptingResponse;

    @Column(columnDefinition = "text")
    private String notAcceptingResponseMessage;

    private Instant stopAcceptingResponseOn;

    private Integer stopAcceptingResponseAfterResponse;

    @Column(nullable = false)
    private Instant lastOpenedOn;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "form")
    private List<Checkbox> checkboxes = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "form")
    private List<Dropdown> dropdowns = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "form")
    private List<FileUpload> fileUploads = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "form")
    private List<LinearScale> linearScales = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "form")
    private List<MultipleChoice> multipleChoices = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "form")
    private List<MultipleChoiceGrid> multipleChoiceGrids = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "form")
    private List<Paragraph> paragraphs = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "form")
    private List<Rating> ratings = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "form")
    private List<ShortAnswer> shortAnswers = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "form")
    private List<TickBoxGrid> tickBoxGrids = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "form")
    private List<Date> dates = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "form")
    private List<Time> times = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "form")
    private List<DateTime> dateTimes = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "form")
    private List<Duration> durations = new ArrayList<>();

}
