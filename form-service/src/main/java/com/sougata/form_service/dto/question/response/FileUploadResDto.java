package com.sougata.form_service.dto.question.response;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.model.FileUpload;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class FileUploadResDto extends QuestionRes {
    private List<FileTypeRes> allowedFileTypes;
    private Integer maxFileSizeInMB;

    public FileUploadResDto(Long id, String question, String description, Boolean required, Integer orderIndex, QuestionType questionType, List<FileTypeRes> allowedFileTypes, Integer maxFileSizeInMB) {
        super(id, question, description, required, orderIndex, questionType);
        this.allowedFileTypes = allowedFileTypes;
        this.maxFileSizeInMB = maxFileSizeInMB;
    }

    public static FileUploadResDto create(FileUpload fileUpload) {
        return new FileUploadResDto(
                fileUpload.getId(),
                fileUpload.getQuestion(),
                fileUpload.getDescription(),
                fileUpload.getRequired(),
                fileUpload.getOrderIndex(),
                QuestionType.FILE_UPLOAD,
                fileUpload.getAllowedFileTypes().stream().map(FileTypeRes::create).toList(),
                fileUpload.getMaxFileSizeInMB()
        );
    }
}

