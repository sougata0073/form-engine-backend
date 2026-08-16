package com.sougata.form_service.template.repository;

import com.sougata.form_service.template.model.DateTemplate;
import org.springframework.stereotype.Repository;

@Repository("DATE_TEMPLATE_REPOSITORY")
public interface DateTemplateRepository extends AnyTypeQuestionTemplateRepository<DateTemplate, Long> {
}