package com.sougata.form_data_service.repository;

import com.sougata.form_data_service.model.LinearScale;
import jakarta.persistence.Tuple;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository("LINEAR_SCALE_RESPONSE_REPOSITORY")
public interface LinearScaleRepository extends AnyTypeQuestionResponseRepository<LinearScale, Long> {

    @Query("""
            select
            ls.questionResponse.questionId questionId,
            ls.scale scale,
            count(ls.scale) responseCount
            from LinearScale ls
            where ls.questionResponse.formResponse.formId = :formId
            group by ls.scale, ls.questionResponse.questionId
            """)
    List<Tuple> getResponseScaleCount(UUID formId);

    @Query("""
            select
            count(distinct ls.scale)
            from LinearScale ls
            where ls.questionResponse.questionId = :questionId and ls.questionResponse.formResponse.formId = :formId
            """)
    Long getDistinctResponseCount(UUID formId, Long questionId);

    @Query(value = """
            select
            ls.scale scale,
            count(ls.question_response_id) responseCount,
            array_agg(fr.id order by fr.created_at) responseIds
            from linear_scales ls
            join question_responses qr
            on qr.id = ls.question_response_id
            join form_responses fr
            on qr.form_response_id = fr.id
            where fr.form_id = :formId and qr.question_id = :questionId
            group by ls.scale
            order by responseCOunt desc
            """, nativeQuery = true)
    List<Tuple> groupedByResponseScale(UUID formId, long questionId, Pageable pageable);

}
