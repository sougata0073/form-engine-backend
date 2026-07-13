package com.sougata.form_data_service.dto.question.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FileUploadResponseAddReqDto extends QuestionResponseAddReq {

    private String fileName;

    private String fileUrl;

    private String fileMimeType;

    @Min(value = 1)
    @Max(value = 1_0485_7600)
    private Integer fileSize;

}
