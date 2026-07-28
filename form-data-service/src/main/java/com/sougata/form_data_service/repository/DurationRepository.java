package com.sougata.form_data_service.repository;

import com.sougata.form_data_service.model.Duration;
import jakarta.persistence.Tuple;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository("DURATION_RESPONSE_REPOSITORY")
public interface DurationRepository extends AnyTypeQuestionResponseRepository<Duration, Long> {

    @Query("""
            select
            d.questionResponse.questionId questionId,
            d.hours hours,
            d.minutes minutes,
            d.seconds seconds
            from Duration d
            where d.questionResponse.formResponse.formId = :formId
            """)
    List<Tuple> getResponseDurations(UUID formId);

    @Query("""
            select
            count(distinct (d.hours, d.minutes, d.seconds))
            from Duration d
            where d.questionResponse.questionId = :questionId and d.questionResponse.formResponse.formId = :formId
            """)
    Long getDistinctResponseCount(UUID formId, Long questionId);

    @Query(value = """
            select
            d.hours hours,
            d.minutes minutes,
            d.seconds seconds,
            count(d.question_response_id) responseCount,
            array_agg(fr.id order by fr.created_at) responseIds
            from durations d
            join question_responses qr
            on qr.id = d.question_response_id
            join form_responses fr
            on qr.form_response_id = fr.id
            where fr.form_id = :formId and qr.question_id = :questionId
            group by d.hours, d.minutes, d.seconds
            order by responseCount desc
            """, nativeQuery = true)
    List<Tuple> groupedByDuration(UUID formId, long questionId, Pageable pageable);

}
