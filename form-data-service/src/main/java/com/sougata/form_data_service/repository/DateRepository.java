package com.sougata.form_data_service.repository;

import com.sougata.form_data_service.model.Date;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository("DATE_RESPONSE_REPOSITORY")
public interface DateRepository extends QuestionResponseRepository<Date, Long> {

    @Query("select d.questionId questionId, d.date date from Date d where d.formResponse.formId = :formId")
    List<Tuple> getResponseDates(UUID formId);

    @Query(value = """
            select
            d.date date,
            count(d.id) responseCount,
            array_agg(fr.id order by fr.created_at) responseIds
            from dates d
            join form_responses fr
            on d.form_response_id = fr.id
            where fr.form_id = :formId and d.question_id = :questionId
            group by d.date
            """, nativeQuery = true)
    List<Tuple> groupedByDate(UUID formId, Long questionId);

}
