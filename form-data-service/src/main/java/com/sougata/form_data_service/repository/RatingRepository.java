package com.sougata.form_data_service.repository;

import com.sougata.form_data_service.model.Rating;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository("RATING_RESPONSE_REPOSITORY")
public interface RatingRepository extends QuestionResponseRepository<Rating, Long> {

    @Query("""
            select
            r.questionId questionId,
            r.rating rating,
            count(r.rating) responseCount
            from Rating r
            where r.formResponse.formId = :formId
            group by r.rating, r.questionId
            """)
    List<Tuple> getResponseRatingCount(UUID formId);

    @Query(value = """
            select
            r.rating rating,
            count(r.id) responseCount,
            array_agg(fr.id order by fr.created_at) responseIds
            from ratings r
            join form_responses fr
            on r.form_response_id = fr.id
            where fr.form_id = :formId and r.question_id = :questionId
            group by r.rating
            """, nativeQuery = true)
    List<Tuple> groupedByRating(UUID formId, Long questionId);
}
