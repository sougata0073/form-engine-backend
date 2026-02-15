package com.sougata.form_service.repository;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.question.response.ParagraphResDto;
import com.sougata.form_service.model.Paragraph;
import org.springframework.stereotype.Repository;

@Repository("PARAGRAPH_REPOSITORY")
public interface ParagraphRepository extends QuestionRepository<Paragraph, Long, ParagraphResDto> {

    @Override
    default QuestionType getQuestionType() {
        return QuestionType.PARAGRAPH;
    }

    @Override
    default ParagraphResDto toQuestionResDto(Paragraph paragraph) {
        return ParagraphResDto.create(paragraph);
    }
}

