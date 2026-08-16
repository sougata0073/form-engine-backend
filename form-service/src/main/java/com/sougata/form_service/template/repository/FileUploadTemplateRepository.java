package com.sougata.form_service.template.repository;

import com.sougata.form_service.template.model.FileUploadTemplate;
import org.springframework.stereotype.Repository;

@Repository("FILE_UPLOAD_TEMPLATE_REPOSITORY")
public interface FileUploadTemplateRepository extends AnyTypeQuestionTemplateRepository<FileUploadTemplate, Long> {
}