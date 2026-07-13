package com.sougata.form_service.service.impl;

import com.sougata.form_service.dto.template.TemplateSummaryResDto;
import com.sougata.form_service.dto.template.TemplateToFormBuildResDto;
import com.sougata.form_service.exception.TemplateNotFoundException;
import com.sougata.form_service.model.Form;
import com.sougata.form_service.model.RecentlyUsedTemplate;
import com.sougata.form_service.model.Template;
import com.sougata.form_service.repository.FormRepository;
import com.sougata.form_service.repository.RecentlyUsedTemplateRepository;
import com.sougata.form_service.repository.TemplateRepository;
import com.sougata.form_service.service.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class TemplateServiceImpl implements TemplateService {

    private final TemplateRepository templateRepository;
    private final FormRepository formRepository;
    private final RecentlyUsedTemplateRepository recentlyUsedTemplateRepository;

    @Autowired
    public TemplateServiceImpl(TemplateRepository templateRepository, FormRepository formRepository, RecentlyUsedTemplateRepository recentlyUsedTemplateRepository) {
        this.templateRepository = templateRepository;
        this.formRepository = formRepository;
        this.recentlyUsedTemplateRepository = recentlyUsedTemplateRepository;
    }

    @Override
    public List<TemplateSummaryResDto> getAllTemplateSummaries(UUID userId) {
        var recentTemplates = recentlyUsedTemplateRepository.getByUserId(userId)
                .stream()
                .map(t ->
                        new TemplateSummaryResDto(t.id(), t.name(), t.categoryName())
                )
                .toList();

        var allTemplates = templateRepository
                .getAllTemplateSummaries()
                .stream()
                .map(t ->
                        new TemplateSummaryResDto(t.id(), t.name(), t.categoryName())
                )
                .toList();

        var merged = new ArrayList<TemplateSummaryResDto>();

        merged.addAll(recentTemplates);
        merged.addAll(allTemplates);

        return merged;
    }

    @Override
    public Template getTemplateById(UUID id) {
        return templateRepository.findById(id)
                .orElseThrow(() -> new TemplateNotFoundException("Template not found with ID: " + id));
    }

    @Override
    @Transactional
    public TemplateToFormBuildResDto buildFormFromTemplate(UUID templateId, UUID userId) {
        Template t = templateRepository.findById(templateId)
                .orElseThrow(() -> new TemplateNotFoundException("Template not found with ID: " + templateId));

        Form f = new Form();
        RecentlyUsedTemplate rt = new RecentlyUsedTemplate();

        f.setUserId(userId);
        f.setName(t.getName());
        f.setTitle(t.getTitle());
        f.setDescription(t.getDescription());
        f.setPublished(false);
        f.setAcceptingResponse(true);
        f.setLastOpenedOn(Instant.now());

        f.setCheckboxes(t.getCheckboxTemplates().stream().map(checkboxTemplate -> checkboxTemplate.fromTemplate(f, checkboxTemplate)).toList());
        f.setDates(t.getDateTemplates().stream().map(dateTemplate -> dateTemplate.fromTemplate(f, dateTemplate)).toList());
        f.setDateTimes(t.getDateTimeTemplates().stream().map(dateTimeTemplate -> dateTimeTemplate.fromTemplate(f, dateTimeTemplate)).toList());
        f.setDropdowns(t.getDropdownTemplates().stream().map(dropdownTemplate -> dropdownTemplate.fromTemplate(f, dropdownTemplate)).toList());
        f.setDurations(t.getDurationTemplates().stream().map(durationTemplate -> durationTemplate.fromTemplate(f, durationTemplate)).toList());
        f.setFileUploads(t.getFileUploadTemplates().stream().map(fileUploadTemplate -> fileUploadTemplate.fromTemplate(f, fileUploadTemplate)).toList());
        f.setLinearScales(t.getLinearScaleTemplates().stream().map(linearScaleTemplate -> linearScaleTemplate.fromTemplate(f, linearScaleTemplate)).toList());
        f.setMultipleChoiceGrids(t.getMultipleChoiceGridTemplates().stream().map(multipleChoiceGridTemplate -> multipleChoiceGridTemplate.fromTemplate(f, multipleChoiceGridTemplate)).toList());
        f.setMultipleChoices(t.getMultipleChoiceTemplates().stream().map(multipleChoiceTemplate -> multipleChoiceTemplate.fromTemplate(f, multipleChoiceTemplate)).toList());
        f.setParagraphs(t.getParagraphTemplates().stream().map(paragraphTemplate -> paragraphTemplate.fromTemplate(f, paragraphTemplate)).toList());
        f.setRatings(t.getRatingTemplates().stream().map(ratingTemplate -> ratingTemplate.fromTemplate(f, ratingTemplate)).toList());
        f.setShortAnswers(t.getShortAnswerTemplates().stream().map(shortAnswerTemplate -> shortAnswerTemplate.fromTemplate(f, shortAnswerTemplate)).toList());
        f.setTickBoxGrids(t.getTickBoxGridTemplates().stream().map(tickBoxGridTemplate -> tickBoxGridTemplate.fromTemplate(f, tickBoxGridTemplate)).toList());
        f.setTimes(t.getTimeTemplates().stream().map(timeTemplate -> timeTemplate.fromTemplate(f, timeTemplate)).toList());

        rt.setUserId(userId);
        rt.setTemplate(t);

        formRepository.save(f);

        var prevTemp = recentlyUsedTemplateRepository.findByUserIdAndTemplateId(userId, templateId);

        if (prevTemp.isPresent()) {
            recentlyUsedTemplateRepository.deleteByUserAndTemplate(userId, templateId);
        } else {
            recentlyUsedTemplateRepository.deleteOld(userId, 5);
        }

        recentlyUsedTemplateRepository.save(rt);

        return new TemplateToFormBuildResDto(f.getId());
    }

}
