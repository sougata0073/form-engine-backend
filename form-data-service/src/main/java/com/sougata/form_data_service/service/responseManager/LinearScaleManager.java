package com.sougata.form_data_service.service.responseManager;

import com.sougata.form_data_service.dto.question.LinearScaleResponseAddReqDto;
import com.sougata.form_data_service.model.LinearScale;
import com.sougata.form_data_service.repository.LinearScaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("LINEAR_SCALE_RESPONSE_MANAGER")
public class LinearScaleManager extends ResponseManager<LinearScaleResponseAddReqDto> {

    private final LinearScaleRepository linearScaleRepository;

    @Autowired
    public LinearScaleManager(LinearScaleRepository linearScaleRepository) {
        this.linearScaleRepository = linearScaleRepository;
    }

    @Override
    public void create(LinearScaleResponseAddReqDto response) {
        LinearScale linearScale = new LinearScale();
        linearScale.setScale(response.getScale());
        linearScale.setQuestionId(response.getQuestionId());

        linearScaleRepository.save(linearScale);
    }
}
