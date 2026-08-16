package com.sougata.form_service.template.repository;

import com.sougata.form_service.template.model.MultipleChoiceTemplate;
import org.springframework.stereotype.Repository;

@Repository("MULTIPLE_CHOICE_TEMPLATE_REPOSITORY")
public interface MultipleChoiceTemplateRepository extends AnyTypeQuestionTemplateRepository<MultipleChoiceTemplate, Long> {
}