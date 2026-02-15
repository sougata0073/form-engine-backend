package com.sougata.form_service.dto.validation.request;

import com.sougata.form_service.constant.ValidationRequestValidationMessages;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FileUploadValidationRequestDto extends ValidationRequest {

    @NotNull(message = ValidationRequestValidationMessages.FILE_URL_NOT_NULL)
    private String fileUrl;

    @NotNull(message = ValidationRequestValidationMessages.FILE_TYPE_NOT_NULL)
    private String fileMimeType;

    @NotNull(message = ValidationRequestValidationMessages.FILE_SIZE_IN_MB_NOT_NULL)
    private Integer fileSizeInMb;

}
