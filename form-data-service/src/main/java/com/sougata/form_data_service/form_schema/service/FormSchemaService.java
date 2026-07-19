package com.sougata.form_data_service.form_schema.service;

import com.sougata.form_data_service.constant.ValidationMessages;
import com.sougata.form_data_service.dto.common.SuccessMessageDto;
import com.sougata.form_data_service.dto.form.FormInfoResDto;
import com.sougata.form_data_service.dto.form.FormResponseDto;
import com.sougata.form_data_service.dto.validation.ResponseValidationRequestDto;
import com.sougata.form_data_service.form_schema.dto.questionSchema.response.QuestionRes;
import com.sougata.form_data_service.form_schema.exception.FormNotFoundException;
import com.sougata.form_data_service.form_schema.exception.RequiredQuestionResponseNotFoundException;
import com.sougata.form_data_service.form_schema.exception.ResponseValidationException;
import com.sougata.form_data_service.form_schema.model.FormSchema;
import com.sougata.form_data_service.form_schema.projection.QuestionIdProjection;
import com.sougata.form_data_service.form_schema.repository.FormSchemaRepository;
import com.sougata.form_data_service.form_schema.repository.QuestionSchemaRepositoryFactory;
import com.sougata.form_data_service.form_schema.service.questionSchemaManager.QuestionSchemaManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional(transactionManager = "formSchemaTransactionManager")
public class FormSchemaService {

    private final FormSchemaRepository formSchemaRepository;
    private final QuestionSchemaRepositoryFactory questionSchemaRepositoryFactory;
    private final QuestionSchemaManagerFactory questionSchemaManagerFactory;

    @Autowired
    public FormSchemaService(FormSchemaRepository formSchemaRepository, QuestionSchemaRepositoryFactory questionSchemaRepositoryFactory, QuestionSchemaManagerFactory questionSchemaManagerFactory) {
        this.formSchemaRepository = formSchemaRepository;
        this.questionSchemaRepositoryFactory = questionSchemaRepositoryFactory;
        this.questionSchemaManagerFactory = questionSchemaManagerFactory;
    }

    public SuccessMessageDto validateResponse(UUID formId, ResponseValidationRequestDto dto) {

        List<Long> requiredQuestionIds = new ArrayList<>();

        // Getting IDs of questions which are marked as required
        questionSchemaRepositoryFactory.getAll().forEach(qRepo -> {
            requiredQuestionIds.addAll(
                    qRepo.findByFormIdAndRequired(formId, true)
                            .stream().map(QuestionIdProjection::getId)
                            .toList()
            );
        });

        Set<Long> responseQuestionIds = new HashSet<>();

        // Putting question IDs of all responses into a HashSet
        dto.responses().forEach(vReq -> responseQuestionIds.add(vReq.getQuestionId()));

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

        // Now passing each response into validators
        dto.responses().forEach(vReq -> {
            var questionManager = questionSchemaManagerFactory.get(vReq.getQuestionType());
            boolean isValid = questionManager.validateResponse(vReq);
            if (!isValid) {
                throw new ResponseValidationException(
                        String.format(ValidationMessages.INVALID_RESPONSE, vReq.getQuestionType())
                );
            }
        });

        return SuccessMessageDto.create("All responses are valid");
    }

    public FormSchema getFormById(UUID id) {
        return formSchemaRepository.findById(id).orElseThrow(() -> new FormNotFoundException(id));
    }

    public FormInfoResDto getFormInfo(UUID formId) {
        FormSchema f = getFormById(formId);

        return FormInfoResDto.create(f);
    }

    public FormResponseDto getFormDetails(UUID id) {
        FormSchema f = getFormById(id);

        List<QuestionRes> questions = new ArrayList<>();

        questionSchemaRepositoryFactory.getAll().forEach(qRepo -> {
            var manager = questionSchemaManagerFactory.get(qRepo.getQuestionType());
            questions.addAll(
                    qRepo.findByFormId(f.getId())
                            .stream()
                            .map(manager::toQuestionResDto)
                            .toList()
            );
        });

        questions.sort(Comparator.comparingInt(QuestionRes::getOrderIndex));

        return new FormResponseDto(
                f.getId(),
                f.getName(),
                f.getTitle(),
                f.getDescription(),
                f.getPublished(),
                f.getAcceptingResponse(),
                f.getNotAcceptingResponseMessage(),
                f.getStopAcceptingResponseOn(),
                f.getStopAcceptingResponseAfterResponse(),
                f.getLastOpenedOn(),
                questions
        );
    }

}
