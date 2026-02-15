package com.sougata.form_data_service.service.responseManager;

import com.sougata.form_data_service.dto.question.MultipleChoiceResponseAddReqDto;
import com.sougata.form_data_service.model.MultipleChoice;
import com.sougata.form_data_service.repository.MultipleChoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("MULTIPLE_CHOICE_RESPONSE_MANAGER")
public class MultipleChoiceManager extends ResponseManager<MultipleChoiceResponseAddReqDto> {

    private final MultipleChoiceRepository multipleChoiceRepository;

    @Autowired
    public MultipleChoiceManager(MultipleChoiceRepository multipleChoiceRepository) {
        this.multipleChoiceRepository = multipleChoiceRepository;
    }

    @Override
    public void create(MultipleChoiceResponseAddReqDto response) {
        MultipleChoice multipleChoice = new MultipleChoice();
        multipleChoice.setResponseIndex(response.getResponseIndex());
        multipleChoice.setQuestionId(response.getQuestionId());

        multipleChoiceRepository.save(multipleChoice);
    }
}
