package com.sougata.form_data_service.service.responseManager;

import com.sougata.form_data_service.dto.question.ParagraphResponseAddReqDto;
import com.sougata.form_data_service.model.Paragraph;
import com.sougata.form_data_service.repository.ParagraphRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("PARAGRAPH_RESPONSE_MANAGER")
public class ParagraphManager extends ResponseManager<ParagraphResponseAddReqDto> {

    private final ParagraphRepository paragraphRepository;

    @Autowired
    public ParagraphManager(ParagraphRepository paragraphRepository) {
        this.paragraphRepository = paragraphRepository;
    }

    @Override
    public void create(ParagraphResponseAddReqDto response) {
        Paragraph paragraph = new Paragraph();
        paragraph.setText(response.getText());
        paragraph.setQuestionId(response.getQuestionId());

        paragraphRepository.save(paragraph);
    }
}
