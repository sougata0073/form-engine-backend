package com.sougata.form_service.template.repository;

import com.sougata.form_service.template.model.MultipleChoiceGridTemplate;
import org.springframework.stereotype.Repository;

@Repository("MULTIPLE_CHOICE_GRID_TEMPLATE_REPOSITORY")
public interface MultipleChoiceGridTemplateRepository extends AnyTypeQuestionTemplateRepository<MultipleChoiceGridTemplate, Long> {
}