package com.sougata.form_data_service.service.responseManager;

import com.sougata.form_data_service.dto.question.FileUploadResponseAddReqDto;
import com.sougata.form_data_service.model.FileUpload;
import com.sougata.form_data_service.repository.FileUploadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("FILE_UPLOAD_RESPONSE_MANAGER")
public class FileUploadManager extends ResponseManager<FileUploadResponseAddReqDto> {

    private final FileUploadRepository fileUploadRepository;

    @Autowired
    public FileUploadManager(FileUploadRepository fileUploadRepository) {
        this.fileUploadRepository = fileUploadRepository;
    }

    @Override
    public void create(FileUploadResponseAddReqDto response) {
        FileUpload fileUpload = new FileUpload();
        fileUpload.setFileUrl(response.getFileUrl());
        fileUpload.setFileMimeType(response.getFileMimeType());
        fileUpload.setFileSizeInMb(response.getFileSizeInMb());
        fileUpload.setQuestionId(response.getQuestionId());

        fileUploadRepository.save(fileUpload);
    }
}
