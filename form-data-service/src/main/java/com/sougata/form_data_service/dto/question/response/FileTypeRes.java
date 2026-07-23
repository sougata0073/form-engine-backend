package com.sougata.form_data_service.dto.question.response;

import java.util.List;

public record FileTypeRes(
        String category,
        List<String> mimeTypes
) {

}