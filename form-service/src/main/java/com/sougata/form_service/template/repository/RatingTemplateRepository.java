package com.sougata.form_service.template.repository;

import com.sougata.form_service.template.model.RatingTemplate;
import org.springframework.stereotype.Repository;

@Repository("RATING_TEMPLATE_REPOSITORY")
public interface RatingTemplateRepository extends AnyTypeQuestionTemplateRepository<RatingTemplate, Long> {
}