package com.sougata.form_data_service.service.responseManager;

import com.sougata.form_data_service.dto.question.ShortAnswerResponseAddReqDto;
import com.sougata.form_data_service.model.ShortAnswer;
import com.sougata.form_data_service.repository.ShortAnswerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("SHORT_ANSWER_RESPONSE_MANAGER")
public class ShortAnswerManager extends ResponseManager<ShortAnswerResponseAddReqDto> {

    private final ShortAnswerRepository shortAnswerRepository;

    @Autowired
    public ShortAnswerManager(ShortAnswerRepository shortAnswerRepository) {
        this.shortAnswerRepository = shortAnswerRepository;
    }

    @Override
    public void create(ShortAnswerResponseAddReqDto response) {
        ShortAnswer shortAnswer = new ShortAnswer();
        shortAnswer.setText(response.getText());
        shortAnswer.setQuestionId(response.getQuestionId());

        shortAnswerRepository.save(shortAnswer);
    }
}
