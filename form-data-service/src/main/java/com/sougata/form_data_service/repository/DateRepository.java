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

    @Query("select d.questionResponse.questionId questionId, d.date date from Date d where d.questionResponse.formResponse.formId = :formId")
    List<Tuple> getResponseDates(UUID formId);

    @Query("""
            select
            count(distinct d.date)
            from Date d
            where d.questionResponse.questionId = :questionId and d.questionResponse.formResponse.formId = :formId
            """)
    Long getDistinctResponseCount(UUID formId, Long questionId);

    @Query(value = """
            select
            d.date date,
            count(d.question_response_id) responseCount,
            array_agg(fr.id order by fr.created_at) responseIds
            from dates d
            join question_responses qr
            on qr.id = d.question_response_id
            join form_responses fr
            on qr.form_response_id = fr.id
            where fr.form_id = :formId and qr.question_id = :questionId
            group by d.date
            order by responseCount desc
            """, nativeQuery = true)
    List<Tuple> groupedByDate(UUID formId, long questionId, Pageable pageable);

}
