package com.sougata.form_service.repository;

import com.sougata.form_service.model.questionSchema.Question;
import com.sougata.form_service.projection.QuestionIdProjection;
import com.sougata.form_service.projection.QuestionSummaryProjection;
import com.sougata.form_service.projection.QuestionTypeProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findByFormId(UUID id);

    List<QuestionTypeProjection> findQuestionTypesByFormId(UUID formId);

    Optional<Question> findByFormIdAndId(UUID formId, Long questionId);

    List<QuestionSummaryProjection> findQuestionSummariesByFormId(UUID formId);

    Optional<QuestionSummaryProjection> findQuestionSummaryByFormIdAndId(UUID formId, Long questionId);

    List<QuestionIdProjection> findByFormIdAndRequired(UUID formId, Boolean required);

    Optional<QuestionTypeProjection> findQuestionTypeByFormIdAndId(UUID formId, Long questionId);

    @Modifying
    @Transactional
    @Query("delete from Question q where q.id = :questionId")
    void deleteQuestion(long questionId);
}
