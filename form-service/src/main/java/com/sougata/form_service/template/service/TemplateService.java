package com.sougata.form_service.template.service;

import com.sougata.form_service.dto.template.TemplateSummariesDto;
import com.sougata.form_service.dto.template.TemplateToFormBuildResDto;
import com.sougata.form_service.template.model.Template;

import java.util.UUID;

public interface TemplateService {
    TemplateSummariesDto getAllTemplateSummaries(UUID userId);
    Template getTemplateById(Long id);
    TemplateToFormBuildResDto buildFormFromTemplate(Long templateId, UUID userId);
}
