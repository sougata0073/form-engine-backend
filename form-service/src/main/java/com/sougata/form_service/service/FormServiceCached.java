package com.sougata.form_service.service;

import com.sougata.form_service.dto.form.FormResponseDto;

import java.util.UUID;

public interface FormServiceCached {

    FormResponseDto getFormDetails(UUID formId);

    FormResponseDto loadFormDetailsFromDb(UUID formId);

}
