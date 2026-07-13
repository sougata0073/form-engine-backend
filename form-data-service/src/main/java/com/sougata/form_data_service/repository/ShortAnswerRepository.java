package com.sougata.form_data_service.repository;

import com.sougata.form_data_service.model.ShortAnswer;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository("SHORT_ANSWER_RESPONSE_REPOSITORY")
public interface ShortAnswerRepository extends QuestionResponseRepository<ShortAnswer, Long> {

    @Query("select sa.questionId questionId, sa.text text from ShortAnswer sa where sa.formResponse.formId = :formId")
    List<Tuple> getResponseTexts(UUID formId);

    @Query(value = """
            select
            sa.text text,
            count(sa.id) responseCount,
            array_agg(fr.id order by fr.created_at) responseIds
            from short_answers sa
            join form_responses fr
            on sa.form_response_id = fr.id
            where fr.form_id = :formId and sa.question_id = :questionId
            group by sa.text
            """, nativeQuery = true)
    List<Tuple> groupedByText(UUID formId, Long questionId);

}
