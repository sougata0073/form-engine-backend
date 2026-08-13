package com.sougata.form_service.template.service.impl;

import com.sougata.form_service.dto.template.TemplateSummaryResDto;
import com.sougata.form_service.template.repository.RecentlyUsedTemplateRepository;
import com.sougata.form_service.template.service.TemplateServiceCached;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TemplateServiceCachedImpl implements TemplateServiceCached {

    private final RecentlyUsedTemplateRepository recentlyUsedTemplateRepository;

    public TemplateServiceCachedImpl(RecentlyUsedTemplateRepository recentlyUsedTemplateRepository) {
        this.recentlyUsedTemplateRepository = recentlyUsedTemplateRepository;
    }

    @Cacheable(cacheNames = {"recentlyUsedTemplates"}, key = "#userId")
    @Override
    public List<TemplateSummaryResDto> getRecentlyUsedTemplates(UUID userId) {
        return recentlyUsedTemplateRepository.getByUserId(userId);
    }
}
