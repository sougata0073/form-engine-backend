package com.sougata.form_data_service.dto.response.summary;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FileUploadResponseSummaryDto extends ResponseSummaryDto {

    private List<Response> responses;

    public record Response(
            String fileName,
            String fileUrl,
            String fileMimeType
    ) {

    }

}
