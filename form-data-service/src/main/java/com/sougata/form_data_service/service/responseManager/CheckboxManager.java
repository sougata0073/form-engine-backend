package com.sougata.form_data_service.service.responseManager;

import com.sougata.form_data_service.dto.question.CheckboxResponseAddReqDto;
import com.sougata.form_data_service.model.Checkbox;
import com.sougata.form_data_service.repository.CheckboxRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("CHECKBOX_RESPONSE_MANAGER")
public class CheckboxManager extends ResponseManager<CheckboxResponseAddReqDto> {

    private final CheckboxRepository checkboxRepository;

    @Autowired
    public CheckboxManager(CheckboxRepository checkboxRepository) {
        this.checkboxRepository = checkboxRepository;
    }

    @Override
    public void create(CheckboxResponseAddReqDto response) {
        Checkbox cb = new Checkbox();
        cb.setResponseIndexes(response.getResponseIndexes().toArray(new Integer[0]));
        cb.setQuestionId(response.getQuestionId());

        checkboxRepository.save(cb);
    }
}
