package com.sougata.form_service.template.repository;

import com.sougata.form_service.template.model.CheckboxTemplate;
import org.springframework.stereotype.Repository;

@Repository("CHECKBOX_TEMPLATE_REPOSITORY")
public interface CheckboxTemplateRepository extends AnyTypeQuestionTemplateRepository<CheckboxTemplate, Long> {
}