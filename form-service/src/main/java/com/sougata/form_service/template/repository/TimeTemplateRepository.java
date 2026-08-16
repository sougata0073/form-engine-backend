package com.sougata.form_service.template.repository;

import com.sougata.form_service.template.model.TimeTemplate;
import org.springframework.stereotype.Repository;

@Repository("TIME_TEMPLATE_REPOSITORY")
public interface TimeTemplateRepository extends AnyTypeQuestionTemplateRepository<TimeTemplate, Long> {
}