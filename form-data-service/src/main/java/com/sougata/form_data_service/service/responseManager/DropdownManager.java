package com.sougata.form_data_service.service.responseManager;

import com.sougata.form_data_service.dto.question.DropdownResponseAddReqDto;
import com.sougata.form_data_service.model.Dropdown;
import com.sougata.form_data_service.repository.DropdownRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("DROPDOWN_RESPONSE_MANAGER")
public class DropdownManager extends ResponseManager<DropdownResponseAddReqDto> {

    private final DropdownRepository dropdownRepository;

    @Autowired
    public DropdownManager(DropdownRepository dropdownRepository) {
        this.dropdownRepository = dropdownRepository;
    }

    @Override
    public void create(DropdownResponseAddReqDto response) {
        Dropdown dropdown = new Dropdown();
        dropdown.setResponseIndex(response.getResponseIndex());
        dropdown.setQuestionId(response.getQuestionId());

        dropdownRepository.save(dropdown);
    }
}
