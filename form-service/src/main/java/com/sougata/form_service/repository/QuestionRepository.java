package com.sougata.form_service.repository;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.question.response.QuestionRes;
import com.sougata.form_service.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface QuestionRepository<Q extends Question, ID, QRD extends QuestionRes> extends JpaRepository<Q, ID> {

    List<Q> findByFormId(UUID id);

    List<Long> findIdByFormIdAndRequired(UUID formId, Boolean required);

    default QuestionType getQuestionType() {
        throw new UnsupportedOperationException(
                "Must be implemented by concrete repository"
        );
    }

    default QRD toQuestionResDto(Q question) {
        throw new UnsupportedOperationException(
                "Must be implemented by concrete repository"
        );
    }
}
