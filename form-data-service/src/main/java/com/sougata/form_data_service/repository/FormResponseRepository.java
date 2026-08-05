package com.sougata.form_data_service.repository;

import com.sougata.form_data_service.dto.form.FormResponseSummaryShortDto;
import com.sougata.form_data_service.model.FormResponse;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FormResponseRepository extends JpaRepository<FormResponse, Long> {

    Optional<FormResponse> findByFormIdAndUserId(UUID formId, UUID userId);

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

    @Query(value = """
            select
            count(fr.id) totalResponseCount,
            array_agg(fr.id order by fr.created_at) responseIds,
            array_agg(fr.user_id order by fr.created_at) userIds
            from form_responses fr
            where fr.form_id = :formId
            group by fr.form_id
            """, nativeQuery = true)
    Tuple getAllResponseIdsAndUserIds(UUID formId);
}
