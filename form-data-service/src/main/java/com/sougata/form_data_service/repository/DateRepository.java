package com.sougata.form_data_service.repository;

import com.sougata.form_data_service.model.Date;
import jakarta.persistence.Tuple;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository("DATE_RESPONSE_REPOSITORY")
public interface DateRepository extends AnyTypeQuestionResponseRepository<Date, Long> {

    @Query(value = """
            select
            x.questionId questionId,
            x.year as year,
            x.month as month,
            array_agg(to_char(x.date, 'YYYY-MM-DD"T"HH24:MI:SSOF') order by x.date) dates,
            array_agg(x.dateCount order by x.date) dateCounts
            from (
                select
                qr.question_id questionId,
                extract(year from d.date)::int as year,
                extract(month from d.date)::int as month,
                d.date date,
                count(d.date) dateCount
                from dates d
                join question_responses qr
                on d.question_response_id = qr.id
                join form_responses fr
                on fr.id = qr.form_response_id
                where fr.form_id = :formId
                group by qr.question_id, year, month, d.date
            ) x
            group by x.questionId, x.year, x.month
            order by x.year, x.month
            """, nativeQuery = true)
    List<Tuple> getResponsesDates(UUID formId, Pageable pageable);

    @Query(value = """
            select
            x.year as year,
            x.month as month,
            array_agg(to_char(x.date, 'YYYY-MM-DD"T"HH24:MI:SSOF') order by x.date) dates,
            array_agg(x.dateCount order by x.date) dateCounts
            from (
                select
                extract(year from d.date)::int as year,
                extract(month from d.date)::int as month,
                d.date date,
                count(d.date) dateCount
                from dates d
                join question_responses qr
                on d.question_response_id = qr.id
                join form_responses fr
                on fr.id = qr.form_response_id
                where fr.form_id = :formId
                and qr.question_id = :questionId
                group by year, month, d.date
            ) x
            group by x.year, x.month
            order by x.year, x.month
            """, nativeQuery = true)
    List<Tuple> getResponseDates(UUID formId, long questionId, Pageable pageable);

    @Query(value = """
            select
            count(distinct coalesce(d.date, '0001-01-01 00:00:00+00'::timestamptz))
            from form_responses fr
            left join question_responses qr
            on fr.id = qr.form_response_id and qr.question_id = :questionId
            left join dates d
            on qr.id = d.question_response_id
            where fr.form_id = :formId
            """, nativeQuery = true)
    Long getDistinctResponseCount(UUID formId, Long questionId);

    @Query(value = """
            select
            d.date date,
            count(*) responseCount
            from form_responses fr
            left join question_responses  qr
            on fr.id = qr.form_response_id
            and qr.question_id = :questionId
            left join dates d
            on qr.id = d.question_response_id
            where fr.form_id = :formId
            group by d.date
            order by responseCount desc, min(fr.created_at) asc
            """, nativeQuery = true)
    List<Tuple> groupedByDate(UUID formId, long questionId, Pageable pageable);

    @Query("""
            select
            d.questionResponse.questionId questionId,
            d.date date
            from Date d
            where d.questionResponse.formResponse.formId = :formId
            and d.questionResponse.formResponse.id = :formResponseId
            """)
    List<Tuple> getDatesByFormResponse(UUID formId, long formResponseId);
}
