package com.sougata.form_data_service.repository;

import com.sougata.form_data_service.model.QuestionResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
public interface QuestionResponseRepository extends JpaRepository<QuestionResponse, Long> {

    @Modifying
    @Transactional
    @Query("""
            delete
            from QuestionResponse qr
            where qr.formResponse.formId = :formId
            and qr.questionId = :questionId
            """)
    void deleteAllByFormIdAndQuestionId(UUID formId, Long questionId);

    @Query("""
            select
            count(qr.id)
            from QuestionResponse qr
            where qr.questionId = :questionId and qr.formResponse.formId = :formId
            """)
    Long getTotalResponseCount(UUID formId, Long questionId);
}
