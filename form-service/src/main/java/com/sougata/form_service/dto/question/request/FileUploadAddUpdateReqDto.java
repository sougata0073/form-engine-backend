package com.sougata.form_service.dto.question.request;

import com.sougata.form_service.constant.ValidationMessages;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class FileUploadAddUpdateReqDto extends QuestionAddUpdateReq {

    @NotNull(message = ValidationMessages.ALLOWED_FILE_CATEGORIES_NOT_NULL)
    private List<String> allowedFileCategories;

    @NotNull(message = ValidationMessages.MAX_FILE_SIZE_NOT_NULL)
    @Min(value = 1, message = ValidationMessages.MIN_FILE_SIZE_RANGE)
    @Max(value = 1_0485_7600, message = ValidationMessages.MAX_FILE_SIZE_RANGE)
    private Integer maxFileSize;

}
