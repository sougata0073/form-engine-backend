package com.sougata.form_service.template.repository;

import com.sougata.form_service.template.model.ShortAnswerTemplate;
import org.springframework.stereotype.Repository;

@Repository("SHORT_ANSWER_TEMPLATE_REPOSITORY")
public interface ShortAnswerTemplateRepository extends AnyTypeQuestionTemplateRepository<ShortAnswerTemplate, Long> {
}