package com.sougata.form_data_service.dto.question;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FileUploadResponseAddReqDto extends QuestionResponseAddReq {

    @NotNull
    private String fileUrl;

    @NotNull
    private String fileMimeType;

    @NotNull
    @Min(value = 1)
    @Max(value = 100)
    private Integer fileSizeInMb;

}
