package com.sougata.form_data_service.repository;

import com.sougata.form_data_service.dto.form.FormResponseSummaryShortDto;
import com.sougata.form_data_service.model.FormResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FormResponseRepository extends JpaRepository<FormResponse, Long> {

    Optional<FormResponse> findByFormIdAndId(UUID formId, Long id);

    @Query("""
            select
            new com.sougata.form_data_service.dto.form.FormResponseSummaryShortDto(
                count(fr.id)
            )
            from FormResponse fr
            where fr.formId = :formId
            """)
    FormResponseSummaryShortDto getFormResponseSummary(UUID formId);

    boolean existsByFormIdAndUserId(UUID formId, UUID userId);

    @Query("""
            select
            x.rn - 1
            from (
                select
                fr.id formResponseId,
                row_number() over (order by fr.createdAt) rn
                from FormResponse fr
                where fr.formId = :formId
            ) x
            where x.formResponseId = :formResponseId
            """)
    Optional<Long> getPageNumberOfFormResponse(UUID formId, long formResponseId);

    @Query("""
            select
            x.formResponseId
            from (
                select
                fr.id formResponseId,
                row_number() over (order by fr.createdAt) rn
                from FormResponse fr
                where fr.formId = :formId
            ) x
            where (x.rn - 1) = :page
            """)
    Optional<Long> getFormResponseIdFromPage(UUID formId, long page);

    @Modifying
    @Transactional
    @Query("delete from FormResponse fr where fr.id = :formResponseId")
    void deleteByFormResponseId(Long formResponseId);

}
