package com.sougata.form_service.repository;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.question.response.QuestionRes;
import com.sougata.form_service.model.AnyTypeQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@NoRepositoryBean
public interface AnyTypeQuestionRepository<Q extends AnyTypeQuestion, ID, QRD extends QuestionRes> extends JpaRepository<Q, ID> {

    Optional<Q> findByQuestion_FormIdAndQuestion_Id(UUID formId, Long questionId);

    @Modifying
    @Transactional
    @Query("delete from #{#entityName} e where e.questionId = :questionId")
    void deleteQuestion(UUID formId, long questionId);

    default QuestionType getQuestionType() {
        throw new UnsupportedOperationException(
                "Must be implemented by concrete repository"
        );
    }
}
