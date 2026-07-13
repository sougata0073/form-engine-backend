package com.sougata.form_data_service.repository;

import com.sougata.form_data_service.model.MultipleChoice;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository("MULTIPLE_CHOICE_RESPONSE_REPOSITORY")
public interface MultipleChoiceRepository extends QuestionResponseRepository<MultipleChoice, Long> {

    @Query("""
            select
            mc.questionId questionId,
            mc.responseOptionId responseOptionId,
            count(mc.responseOptionId) responseCount
            from MultipleChoice mc
            where mc.formResponse.formId = :formId
            group by mc.responseOptionId, mc.questionId
            """)
    List<Tuple> getResponseOptionCount(UUID formId);

}
