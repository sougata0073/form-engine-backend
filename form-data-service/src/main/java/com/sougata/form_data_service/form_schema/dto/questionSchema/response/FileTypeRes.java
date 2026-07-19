package com.sougata.form_data_service.form_schema.dto.questionSchema.response;

import java.util.List;

public record FileTypeRes(
        String category,
        List<String> mimeTypes
) {

}