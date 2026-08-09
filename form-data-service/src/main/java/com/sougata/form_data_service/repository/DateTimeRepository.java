package com.sougata.form_data_service.repository;

import com.sougata.form_data_service.model.DateTime;
import jakarta.persistence.Tuple;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository("DATE_TIME_RESPONSE_REPOSITORY")
public interface DateTimeRepository extends AnyTypeQuestionResponseRepository<DateTime, Long> {

    @Query(value = """
            select
            date(dt.date_time) date,
            dt.date_time time,
            count(dt.date_time) timeCount
            from date_times dt
            join question_responses qr
            on dt.question_response_id = qr.id
            join form_responses fr
            on fr.id = qr.form_response_id
            where fr.form_id = :formId
            and qr.question_id = :questionId
            group by date, dt.date_time
            order by count(qr.id) desc, date asc, dt.date_time asc
            """, nativeQuery = true)
    List<Tuple> getResponseDateTimes(UUID formId, long questionId, Pageable pageable);

    @Query(value = """
            select
            count(distinct coalesce(dt.date_time, '0001-01-01 00:00:00+00'::timestamptz))
            from form_responses fr
            left join question_responses qr
            on fr.id = qr.form_response_id and qr.question_id = :questionId
            left join date_times dt
            on qr.id = dt.question_response_id
            where fr.form_id = :formId
            """, nativeQuery = true)
    Long getDistinctResponseCount(UUID formId, Long questionId);

    @Query(value = """
            select
            dt.date_time dateTime,
            count(*) responseCount
            from form_responses fr
            left join question_responses  qr
            on fr.id = qr.form_response_id
            and qr.question_id = :questionId
            left join date_times dt
            on qr.id = dt.question_response_id
            where fr.form_id = :formId
            group by dt.date_time
            order by responseCount desc, dt.date_time asc
            """, nativeQuery = true)
    List<Tuple> groupedByDateTimes(UUID formId, long questionId, Pageable pageable);

    @Query("""
            select
            d.questionResponse.questionId questionId,
            d.dateTime dateTime
            from DateTime d
            where d.questionResponse.formResponse.formId = :formId
            and d.questionResponse.formResponse.id = :formResponseId
            """)
    List<Tuple> getDateTimesByFormResponse(UUID formId, long formResponseId);

    @Query(value = """
            select
            fr.id responseId,
            fr.user_id userId
            from form_responses fr
            left join question_responses qr
            on fr.id = qr.form_response_id
            and qr.question_id = :questionId
            left join date_times dt
            on qr.id = dt.question_response_id
            where fr.form_id = :formId and (
                (:response is null and dt.date_time is null)
                or dt.date_time = :response
            )
            order by fr.created_at
            """, nativeQuery = true)
    List<Tuple> getResponseIdsByGroupedResponse(UUID formId, long questionId, Instant response, Pageable pageable);
}
