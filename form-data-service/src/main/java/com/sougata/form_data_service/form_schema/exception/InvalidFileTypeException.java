package com.sougata.form_data_service.form_schema.exception;

import com.sougata.form_data_service.constant.ExceptionMessages;
import com.sougata.form_data_service.form_schema.model.FileType;

import java.util.List;

public class InvalidFileTypeException extends RuntimeException {
    public InvalidFileTypeException(String uploadedFileType, List<FileType> allowedFileTypes) {
        super(String.format(ExceptionMessages.INVALID_FILE_TYPE, uploadedFileType, allowedFileTypes));
    }
}
