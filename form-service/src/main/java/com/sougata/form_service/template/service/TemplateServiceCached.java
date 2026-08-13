package com.sougata.form_service.template.service;

import com.sougata.form_service.dto.template.TemplateSummaryResDto;

import java.util.List;
import java.util.UUID;

public interface TemplateServiceCached {
    List<TemplateSummaryResDto> getRecentlyUsedTemplates(UUID userId);
}
