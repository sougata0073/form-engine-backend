package com.sougata.form_service.projection;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FileTypeProjection {
    private String category;
    private String[] mimeTypes;
}
