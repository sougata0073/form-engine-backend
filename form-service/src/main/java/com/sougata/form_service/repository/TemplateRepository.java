package com.sougata.form_service.repository;

import com.sougata.form_service.model.Template;
import com.sougata.form_service.projection.TemplateSummaryProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TemplateRepository extends JpaRepository<Template, UUID> {

    @Query("""
            select
            new com.sougata.form_service.projection.TemplateSummaryProjection(
                t.id,
                t.name,
                tc.name
            )
            from Template t
            join t.category tc
            """)
    List<TemplateSummaryProjection> getAllTemplateSummaries();

}
