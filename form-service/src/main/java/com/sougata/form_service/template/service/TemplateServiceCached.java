package com.sougata.form_service.template.service;

import com.sougata.form_service.dto.template.TemplateDetails;
import com.sougata.form_service.dto.template.TemplateSummaryResDto;
import com.sougata.form_service.template.model.Template;

import java.util.List;
import java.util.UUID;

public interface TemplateServiceCached {
    List<TemplateSummaryResDto> getRecentlyUsedTemplates(UUID userId);
    TemplateDetails getTemplateDetails(Template template);
}
