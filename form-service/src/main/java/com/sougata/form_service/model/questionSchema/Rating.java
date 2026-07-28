package com.sougata.form_service.model.questionSchema;

import com.sougata.form_service.constant.RatingIcon;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "ratings")
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Rating extends AnyTypeQuestion {

    @Column(nullable = false)
    private Integer maxRatingNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RatingIcon ratingIcon;
}
