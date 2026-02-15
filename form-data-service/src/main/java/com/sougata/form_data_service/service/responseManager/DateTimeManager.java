package com.sougata.form_data_service.service.responseManager;

import com.sougata.form_data_service.dto.question.DateTimeResponseAddReqDto;
import com.sougata.form_data_service.model.DateTime;
import com.sougata.form_data_service.repository.DateTimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("DATE_TIME_RESPONSE_MANAGER")
public class DateTimeManager extends ResponseManager<DateTimeResponseAddReqDto> {

    private final DateTimeRepository dateTimeRepository;

    @Autowired
    public DateTimeManager(DateTimeRepository dateTimeRepository) {
        this.dateTimeRepository = dateTimeRepository;
    }

    @Override
    public void create(DateTimeResponseAddReqDto response) {
        DateTime dateTime = new DateTime();
        dateTime.setDateTime(response.getDateTime());
        dateTime.setQuestionId(response.getQuestionId());

        dateTimeRepository.save(dateTime);
    }
}
