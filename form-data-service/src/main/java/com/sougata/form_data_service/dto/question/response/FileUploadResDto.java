package com.sougata.form_data_service.dto.question.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class FileUploadResDto extends QuestionRes {
    private List<FileTypeRes> allowedFileTypes;
    private Integer maxFileSize;

}

