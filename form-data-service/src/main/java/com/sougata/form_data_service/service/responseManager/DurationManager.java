package com.sougata.form_data_service.service.responseManager;

import com.sougata.form_data_service.dto.question.DurationResponseAddReqDto;
import com.sougata.form_data_service.repository.DurationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("DURATION_RESPONSE_MANAGER")
public class DurationManager extends ResponseManager<DurationResponseAddReqDto> {

    private final DurationRepository durationRepository;

    @Autowired
    public DurationManager(DurationRepository durationRepository) {
        this.durationRepository = durationRepository;
    }

    @Override
    public void create(DurationResponseAddReqDto response) {
        Duration duration = new Duration();
        duration.setHours(response.getHours());
        duration.setMinutes(response.getMinutes());
        duration.setSeconds(response.getSeconds());
        duration.setQuestionId(response.getQuestionId());

        durationRepository.save(duration);
    }
}
