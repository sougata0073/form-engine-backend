package com.sougata.form_service.dto.form;

import com.sougata.form_service.constant.ViewFormErrorReason;

public record ViewFormErrorResDto(
        String formTitle,
        ViewFormErrorReason reason,
        String message
) {
}
