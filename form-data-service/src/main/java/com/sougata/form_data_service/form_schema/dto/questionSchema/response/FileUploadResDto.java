package com.sougata.form_data_service.form_schema.dto.questionSchema.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class FileUploadResDto extends QuestionRes {
    private List<FileTypeRes> allowedFileTypes;
    private Integer maxFileSize;
}

