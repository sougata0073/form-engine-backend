package com.sougata.form_service.template.repository;

import com.sougata.form_service.template.model.DurationTemplate;
import org.springframework.stereotype.Repository;

@Repository("DURATION_TEMPLATE_REPOSITORY")
public interface DurationTemplateRepository extends AnyTypeQuestionTemplateRepository<DurationTemplate, Long> {
}