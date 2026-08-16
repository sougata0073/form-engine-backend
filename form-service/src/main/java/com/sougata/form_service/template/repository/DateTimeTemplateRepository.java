package com.sougata.form_service.template.repository;

import com.sougata.form_service.template.model.DateTimeTemplate;
import org.springframework.stereotype.Repository;

@Repository("DATE_TIME_TEMPLATE_REPOSITORY")
public interface DateTimeTemplateRepository extends AnyTypeQuestionTemplateRepository<DateTimeTemplate, Long> {
}