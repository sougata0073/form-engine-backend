package com.sougata.form_service.service.template;

import com.sougata.form_service.dto.template.TemplateDetails;
import com.sougata.form_service.dto.template.TemplateSummaryResDto;
import com.sougata.form_service.model.template.Template;

import java.util.List;
import java.util.UUID;

public interface TemplateServiceCached {
    List<TemplateSummaryResDto> getRecentlyUsedTemplates(UUID userId);
    TemplateDetails getTemplateDetails(Template template);
}
