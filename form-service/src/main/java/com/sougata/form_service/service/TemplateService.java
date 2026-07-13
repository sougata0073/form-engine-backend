package com.sougata.form_service.service;

import com.sougata.form_service.dto.template.TemplateSummaryResDto;
import com.sougata.form_service.dto.template.TemplateToFormBuildResDto;
import com.sougata.form_service.model.Template;

import java.util.List;
import java.util.UUID;

public interface TemplateService {
    List<TemplateSummaryResDto> getAllTemplateSummaries(UUID userId);
    Template getTemplateById(UUID id);
    TemplateToFormBuildResDto buildFormFromTemplate(UUID templateId, UUID userId);
}
