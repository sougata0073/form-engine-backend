package com.sougata.form_data_service.repository;

import com.sougata.form_data_service.model.Duration;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository("DURATION_RESPONSE_REPOSITORY")
public interface DurationRepository extends QuestionResponseRepository<Duration, Long> {

    @Query("""
            select
            d.questionId questionId,
            d.hours hours,
            d.minutes minutes,
            d.seconds seconds
            from Duration d
            where d.formResponse.formId = :formId
            """)
    List<Tuple> getResponseDurations(UUID formId);

    @Query(value = """
            select
            d.hours hours,
            d.minutes minutes,
            d.seconds seconds,
            count(d.id) responseCount,
            array_agg(fr.id order by fr.created_at) responseIds
            from durations d
            join form_responses fr
            on d.form_response_id = fr.id
            where fr.form_id = :formId and d.question_id = :questionId
            group by d.hours, d.minutes, d.seconds
            """, nativeQuery = true)
    List<Tuple> groupedByDuration(UUID formId, Long questionId);

}
