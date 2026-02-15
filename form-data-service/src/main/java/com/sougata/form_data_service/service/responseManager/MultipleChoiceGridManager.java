package com.sougata.form_data_service.service.responseManager;

import com.sougata.form_data_service.dto.question.MultipleChoiceGridResponseAddReqDto;
import com.sougata.form_data_service.model.MultipleChoiceGrid;
import com.sougata.form_data_service.repository.MultipleChoiceGridRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("MULTIPLE_CHOICE_GRID_RESPONSE_MANAGER")
public class MultipleChoiceGridManager extends ResponseManager<MultipleChoiceGridResponseAddReqDto> {

    private final MultipleChoiceGridRepository multipleChoiceGridRepository;

    @Autowired
    public MultipleChoiceGridManager(MultipleChoiceGridRepository multipleChoiceGridRepository) {
        this.multipleChoiceGridRepository = multipleChoiceGridRepository;
    }

    @Override
    public void create(MultipleChoiceGridResponseAddReqDto response) {
        MultipleChoiceGrid multipleChoiceGrid = new MultipleChoiceGrid();

        multipleChoiceGrid.setQuestionId(response.getQuestionId());
        multipleChoiceGrid.setRows(response.getRows().toArray(new Integer[0]));

        multipleChoiceGridRepository.save(multipleChoiceGrid);
    }
}
