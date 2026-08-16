package com.sougata.form_service.template.repository;

import com.sougata.form_service.template.model.LinearScaleTemplate;
import org.springframework.stereotype.Repository;

@Repository("LINEAR_SCALE_TEMPLATE_REPOSITORY")
public interface LinearScaleTemplateRepository extends AnyTypeQuestionTemplateRepository<LinearScaleTemplate, Long> {
}