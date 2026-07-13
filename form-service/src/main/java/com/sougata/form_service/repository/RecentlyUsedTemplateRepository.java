package com.sougata.form_service.repository;

import com.sougata.form_service.model.RecentlyUsedTemplate;
import com.sougata.form_service.projection.TemplateSummaryProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RecentlyUsedTemplateRepository extends JpaRepository<RecentlyUsedTemplate, Long> {

    @Query("""
            select
            new com.sougata.form_service.projection.TemplateSummaryProjection(
                t.id,
                t.name,
                'Recently used'
            )
            from RecentlyUsedTemplate rt
            join rt.template t
            join t.category tc
            where rt.userId = :userId
            order by rt.createdAt desc
            """)
    List<TemplateSummaryProjection> getByUserId(UUID userId);

    @Modifying
    @Query(value = """
            DELETE FROM recently_used_templates
            WHERE id IN (
                SELECT id
                FROM (
                    SELECT
                        id,
                        ROW_NUMBER() OVER (
                            PARTITION BY user_id
                            ORDER BY created_at DESC
                        ) rn
                    FROM recently_used_templates
                    WHERE user_id = :userId
                ) t
                WHERE rn >= :windowSize
            )
            """, nativeQuery = true)
    void deleteOld(UUID userId, int windowSize);

    Optional<RecentlyUsedTemplate> findByUserIdAndTemplateId(UUID userId, UUID templateId);

    @Modifying
    @Query("""
            delete from RecentlyUsedTemplate rt
            where rt.userId = :userId and rt.template.id = :templateId
            """)
    void deleteByUserAndTemplate(UUID userId, UUID templateId);

}
