package com.sougata.form_service.template.repository;

import com.sougata.form_service.template.model.ParagraphTemplate;
import org.springframework.stereotype.Repository;

@Repository("PARAGRAPH_TEMPLATE_REPOSITORY")
public interface ParagraphTemplateRepository extends AnyTypeQuestionTemplateRepository<ParagraphTemplate, Long> {
}