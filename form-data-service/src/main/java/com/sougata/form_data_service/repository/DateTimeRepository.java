package com.sougata.form_data_service.repository;

import com.sougata.form_data_service.model.DateTime;
import jakarta.persistence.Tuple;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository("DATE_TIME_RESPONSE_REPOSITORY")
public interface DateTimeRepository extends AnyTypeQuestionResponseRepository<DateTime, Long> {

    @Query("select dt.questionResponse.questionId questionId, dt.dateTime dateTime from DateTime dt where dt.questionResponse.formResponse.formId = :formId")
    List<Tuple> getResponseDateTimes(UUID formId);

    @Query("""
            select
            count(distinct dt.dateTime)
            from DateTime dt
            where dt.questionResponse.questionId = :questionId and dt.questionResponse.formResponse.formId = :formId
            """)
    Long getDistinctResponseCount(UUID formId, Long questionId);

    @Query(value = """
            select
            dt.date_time dateTime,
            count(dt.question_response_id) responseCount,
            array_agg(fr.id order by fr.created_at) responseIds
            from date_times dt
            join question_responses qr
            on qr.id = dt.question_response_id
            join form_responses fr
            on qr.form_response_id = fr.id
            where fr.form_id = :formId and qr.question_id = :questionId
            group by dt.date_time
            order by responseCount desc
            """, nativeQuery = true)
    List<Tuple> groupedByDateTimes(UUID formId, long questionId, Pageable pageable);
}
