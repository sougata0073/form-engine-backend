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

}
