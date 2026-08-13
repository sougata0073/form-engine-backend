package com.sougata.form_service.template.model;

import com.sougata.form_service.constant.RatingIcon;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "rating_templates")
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RatingTemplate extends AnyQuestionTemplateType {

    @Column(nullable = false)
    private Integer maxRatingNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RatingIcon ratingIcon;

}
