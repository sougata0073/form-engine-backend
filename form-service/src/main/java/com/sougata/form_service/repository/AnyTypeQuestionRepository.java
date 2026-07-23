package com.sougata.form_service.repository;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.question.response.QuestionRes;
import com.sougata.form_service.model.questionSchema.AnyTypeQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AnyTypeQuestionRepository<Q extends AnyTypeQuestion, ID, QRD extends QuestionRes> extends JpaRepository<Q, ID> {

//    List<Q> findByFormId(UUID id);

    Optional<Q> findByQuestion_FormIdAndQuestion_Id(UUID formId, Long questionId);

//    List<QuestionSummaryProjection> findQuestionSummariesByFormId(UUID formId);

//    Optional<QuestionSummaryProjection> findQuestionSummaryByFormIdAndId(UUID formId, Long questionId);

//    List<QuestionIdProjection> findByFormIdAndRequired(UUID formId, Boolean required);

    default QuestionType getQuestionType() {
        throw new UnsupportedOperationException(
                "Must be implemented by concrete repository"
        );
    }
}
