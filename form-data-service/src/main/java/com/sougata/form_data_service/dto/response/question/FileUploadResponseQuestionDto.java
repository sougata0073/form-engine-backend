package com.sougata.form_data_service.dto.response.question;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class FileUploadResponseQuestionDto extends ResponseQuestionDto<FileUploadResponseQuestionDto.Response> {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Response extends ResponseByQuestionResponse {

        private String fileName;
        private String fileUrl;
        private String fileMimeType;

    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Summary extends ResponseByQuestionSummary {
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FormResponsesReqDto {
        private String fileName;
        private String fileUrl;
        private String fileMimeType;
    }

}
