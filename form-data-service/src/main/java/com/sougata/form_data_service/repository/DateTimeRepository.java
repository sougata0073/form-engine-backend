package com.sougata.form_data_service.repository;

import com.sougata.form_data_service.model.DateTime;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository("DATE_TIME_RESPONSE_REPOSITORY")
public interface DateTimeRepository extends QuestionResponseRepository<DateTime, Long> {

    @Query("select dt.questionId questionId, dt.dateTime dateTime from DateTime dt where dt.formResponse.formId = :formId")
    List<Tuple> getResponseDateTimes(UUID formId);

    @Query(value = """
            select
            dt.date_time dateTime,
            count(dt.id) responseCount,
            array_agg(fr.id order by fr.created_at) responseIds
            from date_times dt
            join form_responses fr
            on dt.form_response_id = fr.id
            where fr.form_id = :formId and dt.question_id = :questionId
            group by dt.date_time
            """, nativeQuery = true)
    List<Tuple> groupedByDateTimes(UUID formId, Long questionId);
}
