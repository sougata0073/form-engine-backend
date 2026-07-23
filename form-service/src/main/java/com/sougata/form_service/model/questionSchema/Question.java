package com.sougata.form_service.model.questionSchema;

import com.github.f4b6a3.tsid.TsidCreator;
import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.model.Auditable;
import com.sougata.form_service.model.Form;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "questions")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Question extends Auditable {

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

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private QuestionType questionType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Form form;

//    @OneToOne(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
//    private Checkbox checkbox;
//
//    @OneToOne(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
//    private Dropdown dropdown;
//
//    @OneToOne(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
//    private FileUpload fileUpload;
//
//    @OneToOne(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
//    private LinearScale linearScale;
//
//    @OneToOne(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
//    private MultipleChoice multipleChoice;
//
//    @OneToOne(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
//    private MultipleChoiceGrid multipleChoiceGrid;
//
//    @OneToOne(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
//    private Paragraph paragraph;
//
//    @OneToOne(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
//    private Rating rating;
//
//    @OneToOne(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
//    private ShortAnswer shortAnswer;
//
//    @OneToOne(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
//    private TickBoxGrid tickBoxGrid;
//
//    @OneToOne(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
//    private Date date;
//
//    @OneToOne(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
//    private Time time;
//
//    @OneToOne(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
//    private DateTime dateTime;
//
//    @OneToOne(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
//    private Duration duration;
}
