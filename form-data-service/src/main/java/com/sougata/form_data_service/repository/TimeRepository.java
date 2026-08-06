package com.sougata.form_data_service.repository;

import com.sougata.form_data_service.model.Time;
import jakarta.persistence.Tuple;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository("TIME_RESPONSE_REPOSITORY")
public interface TimeRepository extends AnyTypeQuestionResponseRepository<Time, Long> {

    @Query(value = """
            select
            x.questionId questionId,
            x.hour as hour,
            array_agg(to_char(x.time, 'YYYY-MM-DD"T"HH24:MI:SSOF') order by x.time) times,
            array_agg(x.timeCount order by x.time) timeCounts
            from (
                select
                qr.question_id questionId,
                extract(hour from t.time)::int as hour,
                t.time time,
                count(t.time) timeCount
                from times t
                join question_responses qr
                on t.question_response_id = qr.id
                join form_responses fr
                on fr.id = qr.form_response_id
                where fr.form_id = :formId
                group by qr.question_id, hour, time
            ) x
            group by x.questionId, x.hour
            order by x.hour
            """, nativeQuery = true)
    List<Tuple> getResponsesTimes(UUID formId);

    @Query(value = """
            select
            x.hour as hour,
            array_agg(to_char(x.time, 'YYYY-MM-DD"T"HH24:MI:SSOF') order by x.time) times,
            array_agg(x.timeCount order by x.time) timeCounts
            from (
                select
                extract(hour from t.time)::int as hour,
                t.time time,
                count(t.time) timeCount
                from times t
                join question_responses qr
                on t.question_response_id = qr.id
                join form_responses fr
                on fr.id = qr.form_response_id
                where fr.form_id = :formId
                and qr.question_id = :questionId
                group by hour, time
            ) x
            group by x.hour
            order by x.hour
            """, nativeQuery = true)
    List<Tuple> getResponseTimes(UUID formId, long questionId, Pageable pageable);

    @Query(value = """
            select
            count(distinct coalesce(t.time, '0001-01-01 00:00:00+00'::timestamptz))
            from form_responses fr
            left join question_responses qr
            on fr.id = qr.form_response_id and qr.question_id = :questionId
            left join times t
            on qr.id = t.question_response_id
            where fr.form_id = :formId
            """, nativeQuery = true)
    Long getDistinctResponseCount(UUID formId, Long questionId);

    @Query(value = """
            select
            t.time time,
            count(*) responseCount
            from form_responses fr
            left join question_responses qr
            on fr.id = qr.form_response_id
            and qr.question_id = :questionId
            left join times t
            on qr.id = t.question_response_id
            where fr.form_id = :formId
            group by t.time
            order by responseCount desc, min(fr.created_at) asc
            """, nativeQuery = true)
    List<Tuple> groupedByTime(UUID formId, long questionId, Pageable pageable);

    @Query("""
            select
            t.questionResponse.questionId questionId,
            t.time time
            from Time t
            where t.questionResponse.formResponse.formId = :formId
            and t.questionResponse.formResponse.id = :formResponseId
            """)
    List<Tuple> getTimesByFormResponse(UUID formId, long formResponseId);

}
