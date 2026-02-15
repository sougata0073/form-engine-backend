package com.sougata.form_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
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

    private Long stopAcceptingResponseAfterResponse;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "form")
    private List<Checkbox> checkboxes;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "form")
    private List<Dropdown> dropdowns;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "form")
    private List<FileUpload> fileUploads;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "form")
    private List<LinearScale> linearScales;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "form")
    private List<MultipleChoice> multipleChoices;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "form")
    private List<MultipleChoiceGrid> multipleChoiceGrids;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "form")
    private List<Paragraph> paragraphs;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "form")
    private List<Rating> ratings;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "form")
    private List<ShortAnswer> shortAnswers;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "form")
    private List<TickBoxGrid> tickBoxGrids;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "form")
    private List<Date> dates;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "form")
    private List<Time> times;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "form")
    private List<DateTime> dateTimes;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "form")
    private List<Duration> durations;

}
