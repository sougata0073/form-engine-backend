package com.sougata.form_service.service;

import com.sougata.form_service.dto.common.SuccessMessageDto;
import com.sougata.form_service.dto.form.FormAddUpdateReqDto;
import com.sougata.form_service.dto.form.FormAddUpdateResDto;
import com.sougata.form_service.dto.form.FormResponseDto;
import com.sougata.form_service.dto.validation.request.ResponseValidationRequestDto;
import com.sougata.form_service.model.Form;

import java.util.UUID;

public interface FormService {
    FormAddUpdateResDto createForm(FormAddUpdateReqDto dto);
    FormAddUpdateResDto updateForm(UUID formId, FormAddUpdateReqDto dto);
    FormResponseDto getForm(UUID id);
    FormResponseDto viewForm(UUID id);
    Form getFormById(UUID id);
    SuccessMessageDto validateResponse(UUID formId, ResponseValidationRequestDto dto);
}
