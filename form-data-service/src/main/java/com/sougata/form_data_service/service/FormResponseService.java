package com.sougata.form_data_service.service;

import com.sougata.form_data_service.dto.common.SuccessMessageDto;
import com.sougata.form_data_service.dto.form.FormResponseAddReqDto;
import com.sougata.form_data_service.model.FormResponse;
import com.sougata.form_data_service.repository.FormResponseRepository;
import com.sougata.form_data_service.service.responseManager.ResponseManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FormResponseService {

    private final FormResponseRepository formResponseRepository;
    private final ResponseManagerFactory responseManagerFactory;

    @Autowired
    public FormResponseService(FormResponseRepository formResponseRepository, ResponseManagerFactory responseManagerFactory) {
        this.formResponseRepository = formResponseRepository;
        this.responseManagerFactory = responseManagerFactory;
    }

    @Transactional
    public SuccessMessageDto saveResponse(FormResponseAddReqDto req) {
        FormResponse formResponse = new FormResponse();

        formResponse.setFormId(req.formId());

        // TODO: Make service call to validate responses

        req.responses().forEach(response -> {
            var responseManager = responseManagerFactory.getResponseManager(
                    response.getQuestionType()
            );
            responseManager.create(response);
        });

        formResponseRepository.save(formResponse);

        return SuccessMessageDto.create("Response saved successfully");
    }

}
