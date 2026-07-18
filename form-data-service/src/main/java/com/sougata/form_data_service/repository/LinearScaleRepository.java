package com.sougata.form_data_service.repository;

import com.sougata.form_data_service.model.LinearScale;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository("LINEAR_SCALE_RESPONSE_REPOSITORY")
public interface LinearScaleRepository extends QuestionResponseRepository<LinearScale, Long> {

    @Query("""
            select
            ls.questionId questionId,
            ls.scale scale,
            count(ls.scale) responseCount
            from LinearScale ls
            where ls.formResponse.formId = :formId
            group by ls.scale, ls.questionId
            """)
    List<Tuple> getResponseScaleCount(UUID formId);

    @Query(value = """
            select
            ls.scale scale,
            count(ls.id) responseCount,
            array_agg(fr.id order by fr.created_at) responseIds
            from linear_scales ls
            join form_responses fr
            on ls.form_response_id = fr.id
            where fr.form_id = :formId and ls.question_id = :questionId
            group by ls.scale
            order by responseCOunt desc
            """, nativeQuery = true)
    List<Tuple> groupedByResponseScale(UUID formId, Long questionId);

}
