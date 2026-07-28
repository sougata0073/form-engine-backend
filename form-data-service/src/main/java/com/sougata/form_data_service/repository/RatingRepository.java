package com.sougata.form_data_service.repository;

import com.sougata.form_data_service.model.Rating;
import jakarta.persistence.Tuple;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository("RATING_RESPONSE_REPOSITORY")
public interface RatingRepository extends AnyTypeQuestionResponseRepository<Rating, Long> {

    @Query("""
            select
            r.questionResponse.questionId questionId,
            r.rating rating,
            sum(r.rating) ratingSum,
            count(r.rating) responseCount
            from Rating r
            where r.questionResponse.formResponse.formId = :formId
            and r.rating is not null
            group by r.rating, r.questionResponse.questionId
            """)
    List<Tuple> getResponseRatingCount(UUID formId);

    @Query("""
            select
            count(distinct r.rating)
            from Rating r
            where r.questionResponse.questionId = :questionId and r.questionResponse.formResponse.formId = :formId
            """)
    Long getDistinctResponseCount(UUID formId, Long questionId);

    @Query(value = """
            select
            r.rating rating,
            count(r.question_response_id) responseCount,
            array_agg(fr.id order by fr.created_at) responseIds
            from ratings r
            join question_responses qr
            on qr.id = r.question_response_id
            join form_responses fr
            on qr.form_response_id = fr.id
            where fr.form_id = :formId and qr.question_id = :questionId
            group by r.rating
            order by responseCount desc
            """, nativeQuery = true)
    List<Tuple> groupedByRating(UUID formId, long questionId, Pageable pageable);
}
