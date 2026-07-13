package com.sougata.form_service.model;

import com.sougata.form_service.model.templateSchema.*;
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
@Table(name = "templates")
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Template extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(columnDefinition = "text")
    private String name;

    @Column(columnDefinition = "text")
    private String title;

    @Column(columnDefinition = "text")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private TemplateCategory category;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "template")
    private List<RecentlyUsedTemplate> recentlyUsedTemplates = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "template")
    private List<CheckboxTemplate> checkboxTemplates = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "template")
    private List<DateTemplate> dateTemplates = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "template")
    private List<DateTimeTemplate> dateTimeTemplates = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "template")
    private List<DropdownTemplate> dropdownTemplates = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "template")
    private List<DurationTemplate> durationTemplates = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "template")
    private List<FileUploadTemplate> fileUploadTemplates = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "template")
    private List<LinearScaleTemplate> linearScaleTemplates = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "template")
    private List<MultipleChoiceGridTemplate> multipleChoiceGridTemplates = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "template")
    private List<MultipleChoiceTemplate> multipleChoiceTemplates = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "template")
    private List<ParagraphTemplate> paragraphTemplates = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "template")
    private List<RatingTemplate> ratingTemplates = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "template")
    private List<ShortAnswerTemplate> shortAnswerTemplates = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "template")
    private List<TickBoxGridTemplate> tickBoxGridTemplates = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "template")
    private List<TimeTemplate> timeTemplates = new ArrayList<>();
}
