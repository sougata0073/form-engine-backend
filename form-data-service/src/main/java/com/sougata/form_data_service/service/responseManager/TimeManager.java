package com.sougata.form_data_service.service.responseManager;

import com.sougata.form_data_service.dto.question.TimeResponseAddReqDto;
import com.sougata.form_data_service.model.Time;
import com.sougata.form_data_service.repository.TimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("TIME_RESPONSE_MANAGER")
public class TimeManager extends ResponseManager<TimeResponseAddReqDto> {

    private final TimeRepository timeRepository;

    @Autowired
    public TimeManager(TimeRepository timeRepository) {
        this.timeRepository = timeRepository;
    }

    @Override
    public void create(TimeResponseAddReqDto response) {
        Time time = new Time();
        time.setTime(response.getTime());
        time.setQuestionId(response.getQuestionId());

        timeRepository.save(time);
    }
}
