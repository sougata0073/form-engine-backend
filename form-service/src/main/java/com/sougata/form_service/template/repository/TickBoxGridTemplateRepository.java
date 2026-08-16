package com.sougata.form_service.template.repository;

import com.sougata.form_service.template.model.TickBoxGridTemplate;
import org.springframework.stereotype.Repository;

@Repository("TICK_BOX_GRID_TEMPLATE_REPOSITORY")
public interface TickBoxGridTemplateRepository extends AnyTypeQuestionTemplateRepository<TickBoxGridTemplate, Long> {
}