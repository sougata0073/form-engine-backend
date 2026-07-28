package com.sougata.form_data_service.repository;

import com.sougata.form_data_service.model.AnyTypeQuestionResponse;
import com.sougata.form_data_service.projection.CommonResponseSummaryProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@NoRepositoryBean
public interface AnyTypeQuestionResponseRepository<Q extends AnyTypeQuestionResponse, ID> extends JpaRepository<Q, ID> {

    @Query("""
            select
            new com.sougata.form_data_service.projection.CommonResponseSummaryProjection(
                        qr.questionResponse.questionId, count(qr.questionResponseId)
            )
            from #{#entityName} qr
            where qr.questionResponse.formResponse.formId = :formId
            group by qr.questionResponse.questionId
            """)
    List<CommonResponseSummaryProjection> getResponseSummaries(UUID formId);

    @Query("select qr from #{#entityName} qr where qr.questionResponse.formResponse.formId = :formId and qr.questionResponse.questionId = :questionId")
    List<Q> findByFormIdAndQuestionId(UUID formId, Long questionId);

    @Modifying
    @Transactional
    @Query("""
        delete
        from #{#entityName} e
        where e.questionResponse.formResponse.formId = :formId
        and e.questionResponse.questionId = :questionId
        """)
    void deleteAllByFormIdAndQuestionId(UUID formId, Long questionId);

}
