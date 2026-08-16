package com.sougata.form_service.template.repository;

import com.sougata.form_service.template.model.AnyTypeQuestionTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface AnyTypeQuestionTemplateRepository<QT extends AnyTypeQuestionTemplate, ID> extends JpaRepository<QT, ID> {

}
