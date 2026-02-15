package com.sougata.form_service.projection;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.question.response.FileTypeRes;
import com.sougata.form_service.dto.question.response.FileUploadResDto;
import com.sougata.form_service.model.FileType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FileUploadProjection implements QuestionProjection<FileUploadResDto> {
    private Long id;
    private String question;
    private String description;
    private Boolean required;
    private List<FileType> allowedFileTypes;
    private Integer maxFileSizeInMB;
    private Integer orderIndex;

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.FILE_UPLOAD;
    }

    @Override
    public FileUploadResDto getQuestionResponse() {
        return new FileUploadResDto(
                id,
                question,
                description,
                required,
                orderIndex,
                QuestionType.FILE_UPLOAD,
                allowedFileTypes.stream().map(FileTypeRes::create).toList(),
                maxFileSizeInMB
        );
    }
}
