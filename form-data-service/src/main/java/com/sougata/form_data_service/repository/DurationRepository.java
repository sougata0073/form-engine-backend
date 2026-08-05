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

    @Query(value = """
            select
            x.questionId questionId,
            x.hours as hours,
            array_agg(x.minutes order by x.minutes, x.seconds) minutes,
            array_agg(x.seconds order by x.minutes, x.seconds) seconds,
            array_agg(x.minSecCount order by x.minutes, x.seconds) minSecCounts
            from (
                select
                qr.question_id questionId,
                d.hours hours,
                d.minutes minutes,
                d.seconds seconds,
                count((d.minutes, d.seconds)) minSecCount
                from durations d
                join question_responses qr
                on d.question_response_id = qr.id
                join form_responses fr
                on fr.id = qr.form_response_id
                where fr.form_id = :formId
                group by qr.question_id, d.hours, d.minutes, d.seconds
            ) x
            group by x.questionId, x.hours
            order by x.hours
            """, nativeQuery = true)
    List<Tuple> getResponsesDurations(UUID formId, Pageable pageable);

    @Query(value = """
            select
            x.hours as hours,
            array_agg(x.minutes order by x.minutes, x.seconds) minutes,
            array_agg(x.seconds order by x.minutes, x.seconds) seconds,
            array_agg(x.minSecCount order by x.minutes, x.seconds) minSecCounts
            from (
                select
                d.hours hours,
                d.minutes minutes,
                d.seconds seconds,
                count((d.minutes, d.seconds)) minSecCount
                from durations d
                join question_responses qr
                on d.question_response_id = qr.id
                join form_responses fr
                on fr.id = qr.form_response_id
                where fr.form_id = :formId
                and qr.question_id = :questionId
                group by d.hours, d.minutes, d.seconds
            ) x
            group by x.hours
            order by x.hours
            """, nativeQuery = true)
    List<Tuple> getResponseDurations(UUID formId, long questionId, Pageable pageable);

    @Query(value = """
            select
            count(distinct (coalesce(d.hours, -1), coalesce(d.minutes, -1), coalesce(d.seconds, -1)))
            from form_responses fr
            left join question_responses qr
            on fr.id = qr.form_response_id and qr.question_id = :questionId
            left join durations d
            on qr.id = d.question_response_id
            where fr.form_id = :formId
            """, nativeQuery = true)
    Long getDistinctResponseCount(UUID formId, Long questionId);

    @Query(value = """
            select
            d.hours hours,
            d.minutes minutes,
            d.seconds seconds,
            count(*) responseCount
            from form_responses fr
            left join question_responses  qr
            on fr.id = qr.form_response_id
            and qr.question_id = :questionId
            left join durations d
            on qr.id = d.question_response_id
            where fr.form_id = :formId
            group by d.hours, d.minutes, d.seconds
            order by responseCount desc, min(fr.created_at) asc
            """, nativeQuery = true)
    List<Tuple> groupedByDuration(UUID formId, long questionId, Pageable pageable);

}
