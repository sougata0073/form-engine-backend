package com.sougata.form_service.template.repository;

import com.sougata.form_service.dto.template.TemplateSummaryResDto;
import com.sougata.form_service.template.model.Template;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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

    @Query("select t from Template t where t.id = :templateId")
    Optional<Template> findByTemplateId(Long templateId);
}
