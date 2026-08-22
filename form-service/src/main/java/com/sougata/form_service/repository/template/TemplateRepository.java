package com.sougata.form_service.repository.template;

import com.sougata.form_service.dto.template.TemplateSummaryResDto;
import com.sougata.form_service.model.template.Template;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TemplateRepository extends JpaRepository<Template, Long> {

    @Cacheable(cacheNames = {"allTemplateSummaries"})
    @Query("""
            select
            new com.sougata.form_service.dto.template.TemplateSummaryResDto(
                t.id,
                t.name,
                t.category.name
            )
            from Template t
            """)
    List<TemplateSummaryResDto> getAllTemplateSummaries();

}
