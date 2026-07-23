package com.sougata.form_service.service.questionManager;

import com.sougata.form_service.constant.ExceptionMessages;
import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.question.request.LinearScaleAddUpdateReqDto;
import com.sougata.form_service.dto.question.response.LinearScaleResDto;
import com.sougata.form_service.dto.validation.request.LinearScaleValidationRequestDto;
import com.sougata.form_service.exception.QuestionNotFoundException;
import com.sougata.form_service.exception.ResponseValidationException;
import com.sougata.form_service.model.questionSchema.Checkbox;
import com.sougata.form_service.model.questionSchema.LinearScale;
import com.sougata.form_service.model.questionSchema.Question;
import com.sougata.form_service.repository.LinearScaleRepository;
import com.sougata.form_service.repository.QuestionRepository;
import com.sougata.form_service.service.FormService;
import com.sougata.form_service.service.QuestionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service("LINEAR_SCALE_QUESTION_MANAGER")
public class LinearScaleManager extends QuestionManager<LinearScale, LinearScaleAddUpdateReqDto, LinearScaleResDto, LinearScaleValidationRequestDto> {

    private final LinearScaleRepository linearScaleRepository;

    public LinearScaleManager(LinearScaleRepository linearScaleRepository, FormService formService, QuestionRepository questionRepository) {
        super(questionRepository, formService);
        this.linearScaleRepository = linearScaleRepository;
    }

    @Override
    public LinearScaleResDto get(UUID formId, Long questionId) {
        return toQuestionResDto(linearScaleRepository.findByQuestion_FormIdAndQuestion_Id(formId, questionId).orElseThrow(() -> new QuestionNotFoundException(questionId)));
    }

    @Override
    @Transactional
    public LinearScaleResDto create(UUID formId, LinearScaleAddUpdateReqDto crudDto) {
        var newLs = new LinearScale();

        var question = createQuestion(crudDto, formId);

        setPropertiesForNew(crudDto, newLs, question);

        var saved = linearScaleRepository.save(newLs);

        return toQuestionResDto(saved);
    }

    @Override
    public LinearScaleResDto create(UUID formId, Long questionId, LinearScaleAddUpdateReqDto crudDto) {
        var newCb = new LinearScale();

        var question = updateQuestion(questionId, crudDto);

        setPropertiesForNew(crudDto, newCb, question);

        var saved = linearScaleRepository.save(newCb);

        return toQuestionResDto(saved);
    }

    @Override
    @Transactional
    public LinearScaleResDto update(Long questionId, LinearScaleAddUpdateReqDto crudDto) {
        LinearScale ls = linearScaleRepository.findById(questionId)
                .orElseThrow(() -> new QuestionNotFoundException(QuestionType.LINEAR_SCALE, questionId));

        updateQuestion(questionId, crudDto);

        ls.setFromNumber(crudDto.getFromNumber());
        ls.setToNumber(crudDto.getToNumber());

        linearScaleRepository.save(ls);

        return toQuestionResDto(ls);
    }

    @Override
    public LinearScaleResDto toQuestionResDto(LinearScale question) {
        var ls = new LinearScaleResDto();

        populateCommonFields(question, ls);

        ls.setFromNumber(question.getFromNumber());
        ls.setToNumber(question.getToNumber());

        return ls;
    }

    @Override
    public boolean validateResponse(LinearScaleValidationRequestDto validationDto) {
        Integer toNumber = linearScaleRepository.getToNumber(validationDto.getQuestionId())
                .orElseThrow(() -> new QuestionNotFoundException(QuestionType.LINEAR_SCALE, validationDto.getQuestionId()));

        if (validationDto.getScale() > toNumber) {
            throw new ResponseValidationException(
                    String.format(
                            ExceptionMessages.INVALID_SCALE, toNumber, validationDto.getScale()
                    )
            );
        }

        return true;
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.LINEAR_SCALE;
    }

    @Override
    public void delete(Long questionId) {
        linearScaleRepository.deleteById(questionId);
    }

    private void setPropertiesForNew(LinearScaleAddUpdateReqDto source, LinearScale target, Question question) {
        target.setQuestion(question);
        target.setFromNumber(source.getFromNumber());
        target.setToNumber(source.getToNumber());
    }
}
