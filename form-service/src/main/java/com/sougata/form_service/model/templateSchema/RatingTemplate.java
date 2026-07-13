package com.sougata.form_service.model.templateSchema;

import com.sougata.form_service.constant.RatingIcon;
import com.sougata.form_service.model.Form;
import com.sougata.form_service.model.questionSchema.Rating;
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
public class RatingTemplate extends QuestionTemplate<RatingTemplate> {

    @Column(nullable = false)
    private Integer maxRatingNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RatingIcon ratingIcon;

    @Override
    public Rating fromTemplate(Form f, RatingTemplate t) {
        var rating = new Rating();

        rating.setQuestion(t.getQuestion());
        rating.setDescription(t.getDescription());
        rating.setRequired(t.getRequired());
        rating.setForm(f);
        rating.setOrderIndex(t.getOrderIndex());
        rating.setMaxRatingNumber(t.getMaxRatingNumber());
        rating.setRatingIcon(t.getRatingIcon());

        return rating;
    }

}
