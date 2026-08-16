package com.sougata.form_service.repository;

import com.sougata.form_service.model.Question;
import com.sougata.form_service.projection.QuestionIdAndTypeProjection;
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

    List<Question> findAllByFormId(UUID formId);

    List<QuestionSummaryProjection> findQuestionSummariesByFormId(UUID formId);

    List<QuestionIdAndTypeProjection> findQuestionIdAndTypesByFormId(UUID formId);

    List<QuestionIdProjection> findQuestionIdsByFormId(UUID formId);

    Optional<QuestionSummaryProjection> findQuestionSummaryById(Long id);

    Optional<QuestionTypeProjection> findQuestionTypeById(Long id);

    Optional<QuestionIdAndTypeProjection> findQuestionIdAndTypeById(Long id);


    @Modifying
    @Transactional(transactionManager = "schemaTransactionManager")
    @Query("delete from Question q where q.id = :questionId")
    void deleteQuestion(long questionId);

    @Modifying
    @Transactional(transactionManager = "schemaTransactionManager")
    @Query(value = """
            update
            questions q
            set order_index = q.order_index - 1
            from forms f
            where f.id = q.form_id
            and q.order_index > :orderIndex
            and f.id = :formId
            """, nativeQuery = true)
    void setQuestionOrderAfterDeleteQuestion(UUID formId, int orderIndex);

    @Query("select cast(count(q.id) as int) from Question q where q.form.id = :formId")
    Integer getNextQuestionIndex(UUID formId);

    @Modifying
    @Transactional(transactionManager = "schemaTransactionManager")
    @Query(value = """
            update
            questions q
            set order_index = case
                when :prevIndex > :currIndex then (
                    case
                        when q.order_index >= :currIndex and q.order_index < :prevIndex then q.order_index + 1
                        else q.order_index
                    end
                )
                when :prevIndex < :currIndex then (
                    case
                        when q.order_index > :prevIndex and q.order_index <= :currIndex then q.order_index - 1
                        else q.order_index
                    end
                )
                else q.order_index
            end
            from forms f
            where f.id = q.form_id
            and q.id != :questionId
            and f.id = :formId
            """, nativeQuery = true)
    void updateNextQuestionOrderIndexes(UUID formId, long questionId, int prevIndex, int currIndex);
}
