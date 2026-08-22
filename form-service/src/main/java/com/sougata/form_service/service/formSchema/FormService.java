package com.sougata.form_service.service.formSchema;

import com.sougata.form_service.dto.common.SuccessMessageDto;
import com.sougata.form_service.dto.form.*;
import com.sougata.form_service.dto.template.TemplateDetails;
import com.sougata.form_service.model.formSchema.Form;

import java.util.UUID;

public interface FormService {
    FormInfoResDto createForm(FormAddUpdateReqDto dto, UUID userId);

    FormInfoResDto copyForm(UUID formId, CopyFormReqDto req, UUID userId);

    FormInfoResDto updateForm(UUID formId, FormAddUpdateReqDto dto, UUID userId);

    FormResponseDto getForm(UUID formId, UUID userId);

    FormResponseDto viewForm(UUID id, UUID userId);

    FormSummariesRes getFormsSummaries(UUID userId);

    Form getFormById(UUID id);

    SuccessMessageDto renameForm(UUID formId, FormRenameReqDto dto, UUID userId);

    FormInfoResDto getFormInfo(UUID formId);

    SuccessMessageDto deleteForm(UUID formId, UUID userId);

    Form createFromTemplate(TemplateDetails template, UUID userId);

}
