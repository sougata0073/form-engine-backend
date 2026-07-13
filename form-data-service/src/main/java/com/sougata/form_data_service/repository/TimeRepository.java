package com.sougata.form_data_service.repository;

import com.sougata.form_data_service.model.Time;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository("TIME_RESPONSE_REPOSITORY")
public interface TimeRepository extends QuestionResponseRepository<Time, Long> {

    @Query("select t.questionId questionId, t.time time from Time t where t.formResponse.formId = :formId")
    List<Tuple> getResponseTimes(UUID formId);

    @Query(value = """
            select
            t.time time,
            count(t.id) responseCount,
            array_agg(fr.id order by fr.created_at) responseIds
            from times t
            join form_responses fr
            on t.form_response_id = fr.id
            where fr.form_id = :formId and t.question_id = :questionId
            group by t.time
            """, nativeQuery = true)
    List<Tuple> groupedByTime(UUID formId, Long questionId);

}
