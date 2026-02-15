package com.sougata.form_data_service.service.responseManager;

import com.sougata.form_data_service.dto.question.DateResponseAddReqDto;
import com.sougata.form_data_service.model.Date;
import com.sougata.form_data_service.repository.DateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("DATE_RESPONSE_MANAGER")
public class DateManager extends ResponseManager<DateResponseAddReqDto> {

    private final DateRepository dateRepository;

    @Autowired
    public DateManager(DateRepository dateRepository) {
        this.dateRepository = dateRepository;
    }

    @Override
    public void create(DateResponseAddReqDto response) {
        Date date = new Date();
        date.setDate(response.getDate());
        date.setQuestionId(response.getQuestionId());

        dateRepository.save(date);
    }
}
