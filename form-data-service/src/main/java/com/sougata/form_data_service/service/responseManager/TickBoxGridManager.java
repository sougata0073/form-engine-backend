package com.sougata.form_data_service.service.responseManager;

import com.sougata.form_data_service.dto.question.TickBoxGridResponseAddReqDto;
import com.sougata.form_data_service.model.TickBoxGrid;
import com.sougata.form_data_service.repository.TickBoxGridRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("TICK_BOX_GRID_RESPONSE_MANAGER")
public class TickBoxGridManager extends ResponseManager<TickBoxGridResponseAddReqDto> {

    private final TickBoxGridRepository tickBoxGridRepository;

    @Autowired
    public TickBoxGridManager(TickBoxGridRepository tickBoxGridRepository) {
        this.tickBoxGridRepository = tickBoxGridRepository;
    }

    @Override
    public void create(TickBoxGridResponseAddReqDto response) {
        TickBoxGrid tickBoxGrid = new TickBoxGrid();

        tickBoxGrid.setQuestionId(response.getQuestionId());

        Integer[][] rows = response.getRows()
                .stream()
                .map(row -> row.toArray(new Integer[0]))
                .toArray(Integer[][]::new);

        tickBoxGrid.setRows(rows);

        tickBoxGridRepository.save(tickBoxGrid);
    }
}
