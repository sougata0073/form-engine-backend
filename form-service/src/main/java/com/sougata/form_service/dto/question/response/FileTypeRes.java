package com.sougata.form_service.dto.question.response;

import com.sougata.form_service.model.FileType;

import java.util.Arrays;
import java.util.List;

public record FileTypeRes(
        String category,
        List<String> mimeTypes
) {

    public static FileTypeRes create(FileType fileType) {
        return new FileTypeRes(
                fileType.getCategory(),
                Arrays.asList(fileType.getMimeTypes())
        );
    }

}
