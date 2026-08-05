package com.sougata.form_data_service.form_schema.service;

import com.sougata.form_data_service.constant.ValidationMessages;
import com.sougata.form_data_service.dto.common.SuccessMessageDto;
import com.sougata.form_data_service.dto.question.response.QuestionRes;
import com.sougata.form_data_service.dto.validation.ResponseValidationRequestDto;
import com.sougata.form_data_service.feignClient.FormServiceFeignClient;
import com.sougata.form_data_service.form_schema.exception.QuestionSchemaNotFoundException;
import com.sougata.form_data_service.form_schema.exception.RequiredQuestionResponseNotFoundException;
import com.sougata.form_data_service.form_schema.exception.ResponseValidationException;
import com.sougata.form_data_service.form_schema.service.questionSchemaManager.QuestionSchemaManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FormSchemaService {

    private final QuestionSchemaManagerFactory questionSchemaManagerFactory;
    private final FormServiceFeignClient formServiceFeignClient;

    @Autowired
    public FormSchemaService(QuestionSchemaManagerFactory questionSchemaManagerFactory, FormServiceFeignClient formServiceFeignClient) {
        this.formServiceFeignClient = formServiceFeignClient;
        this.questionSchemaManagerFactory = questionSchemaManagerFactory;
    }

    public SuccessMessageDto validateResponse(UUID formId, ResponseValidationRequestDto dto) {

        var questionResList = formServiceFeignClient.getFormDetails(formId).getQuestions();

        // Getting IDs of questions which are marked as required
        List<Long> requiredQuestionIds = questionResList.stream().filter(QuestionRes::getRequired).map(QuestionRes::getId).toList();

        Set<Long> responseQuestionIds = new HashSet<>();

        // Putting question IDs of all responses into a HashSet
        dto.getResponses().forEach(vReq -> responseQuestionIds.add(vReq.getQuestionId()));

        List<String> missingQuestionIds = new ArrayList<>();

        // Checking if any ID is missing in the response question ID HashSet
        // If true put it in the missing list
        requiredQuestionIds.forEach(id -> {
            if (!responseQuestionIds.contains(id)) {
                missingQuestionIds.add(id.toString());
            }
        });

        if (!missingQuestionIds.isEmpty()) {
            throw new RequiredQuestionResponseNotFoundException(missingQuestionIds);
        }

        var questionResMap = questionResList.stream().collect(Collectors.groupingBy(QuestionRes::getId));

        // Now passing each response into validators
        dto.getResponses().forEach(vReq -> {
            var questionManager = questionSchemaManagerFactory.get(vReq.getQuestionType());
            boolean isValid = questionManager.validateResponse(vReq,
                    questionResMap.get(vReq.getQuestionId()).stream().findFirst()
                            .orElseThrow(() -> new QuestionSchemaNotFoundException(vReq.getQuestionType(), vReq.getQuestionId()))
            );
            if (!isValid) {
                throw new ResponseValidationException(
                        String.format(ValidationMessages.INVALID_RESPONSE, vReq.getQuestionType())
                );
            }
        });

        return SuccessMessageDto.create("All responses are valid");
    }


}
