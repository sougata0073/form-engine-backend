package com.sougata.form_service.service;

import com.sougata.form_service.dto.common.SuccessMessageDto;
import com.sougata.form_service.dto.form.*;
import com.sougata.form_service.dto.validation.request.ResponseValidationRequestDto;
import com.sougata.form_service.model.Form;

import java.util.List;
import java.util.UUID;

public interface FormService {
    FormInfoResDto createForm(FormAddUpdateReqDto dto, UUID userId);

    FormInfoResDto updateForm(UUID formId, FormAddUpdateReqDto dto, UUID userId);

    FormResponseDto getFormDetails(UUID id);

    FormResponseDto getForm(UUID id);

    FormResponseDto viewForm(UUID id, UUID userId);

    List<FormSummaryResDto> getFormsSummaries(UUID userId);

    Form getFormById(UUID id);

    SuccessMessageDto validateResponse(UUID formId, ResponseValidationRequestDto dto);

    SuccessMessageDto renameForm(UUID formId, FormRenameReqDto dto);

    FormInfoResDto getFormInfo(UUID formId);
}
