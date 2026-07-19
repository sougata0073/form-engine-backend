package com.sougata.form_data_service.repository;

import com.sougata.form_data_service.model.QuestionResponse;
import com.sougata.form_data_service.projection.CommonResponseSummaryProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.UUID;

@NoRepositoryBean
public interface QuestionResponseRepository<Q extends QuestionResponse, ID> extends JpaRepository<Q, ID> {

    @Query("""
            select
            new com.sougata.form_data_service.projection.CommonResponseSummaryProjection(
                        qr.questionId, count(qr.id)
            )
            from #{#entityName} qr
            where qr.formResponse.formId = :formId
            group by qr.questionId
            """)
    List<CommonResponseSummaryProjection> getResponseSummaries(UUID formId);

    @Query("select qr from #{#entityName} qr where qr.formResponse.formId = :formId and qr.questionId = :questionId")
    List<Q> findByFormIdAndQuestionId(UUID formId, Long questionId);

//    @Query("""
//            select
//            count(qr.id)
//            from #{#entityName} qr
//            where qr.questionId = :questionId and qr.formResponse.formId = :formId
//            group by qr.questionId
//            """)
//    Long getTotalResponseCount(UUID formId, Long questionId);

}
