package com.sougata.form_service.service.questionManager;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.question.request.MultipleChoiceAddUpdateReqDto;
import com.sougata.form_service.dto.question.response.MultipleChoiceResDto;
import com.sougata.form_service.dto.validation.request.MultipleChoiceValidationRequestDto;
import com.sougata.form_service.exception.QuestionNotFoundException;
import com.sougata.form_service.exception.ResponseValidationException;
import com.sougata.form_service.model.questionSchema.MultipleChoice;
import com.sougata.form_service.model.questionSchema.MultipleChoiceOption;
import com.sougata.form_service.model.questionSchema.Question;
import com.sougata.form_service.repository.MultipleChoiceRepository;
import com.sougata.form_service.repository.QuestionRepository;
import com.sougata.form_service.service.FormService;
import com.sougata.form_service.service.QuestionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service("MULTIPLE_CHOICE_QUESTION_MANAGER")
public class MultipleChoiceManager extends QuestionManager<MultipleChoice, MultipleChoiceAddUpdateReqDto, MultipleChoiceResDto, MultipleChoiceValidationRequestDto> {

    private final MultipleChoiceRepository multipleChoiceRepository;

    public MultipleChoiceManager(MultipleChoiceRepository multipleChoiceRepository, FormService formService, QuestionRepository questionRepository) {
        super(questionRepository, formService);
        this.multipleChoiceRepository = multipleChoiceRepository;
    }

    @Override
    public MultipleChoiceResDto get(UUID formId, Long questionId) {
        return toQuestionResDto(multipleChoiceRepository.findByQuestion_FormIdAndQuestion_Id(formId, questionId).orElseThrow(() -> new QuestionNotFoundException(questionId)));
    }

    @Override
    @Transactional
    public MultipleChoiceResDto create(UUID formId, MultipleChoiceAddUpdateReqDto crudDto) {
        var newMc = new MultipleChoice();

        var question = createQuestion(crudDto, formId);

        setPropertiesForNew(crudDto, newMc, question);

        var saved = multipleChoiceRepository.save(newMc);

        return toQuestionResDto(saved);
    }

    @Override
    public MultipleChoiceResDto create(UUID formId, Long questionId, MultipleChoiceAddUpdateReqDto crudDto) {
        var newMc = new MultipleChoice();

        var question = updateQuestion(questionId, crudDto);

        setPropertiesForNew(crudDto, newMc, question);

        var saved = multipleChoiceRepository.save(newMc);

        return toQuestionResDto(saved);
    }

    @Override
    @Transactional
    public MultipleChoiceResDto update(Long questionId, MultipleChoiceAddUpdateReqDto crudDto) {
        MultipleChoice mc = multipleChoiceRepository.findById(questionId)
                .orElseThrow(() -> new QuestionNotFoundException(QuestionType.MULTIPLE_CHOICE, questionId));

        updateQuestion(questionId, crudDto);

        Map<Long, MultipleChoiceOption> existingOptions = mc.getOptions().stream()
                .collect(Collectors.toMap(MultipleChoiceOption::getId, option -> option));

        Set<Long> requestOptionIds = crudDto.getOptions().stream()
                .map(MultipleChoiceAddUpdateReqDto.Option::id)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        mc.getOptions().removeIf(option -> !requestOptionIds.contains(option.getId()));

        for (int i = 0; i < crudDto.getOptions().size(); i++) {
            var dto = crudDto.getOptions().get(i);

            if (dto.id() == null) {
                MultipleChoiceOption option = new MultipleChoiceOption();
                option.setOption(dto.option());
                option.setOrderIndex(i);
                option.setMultipleChoice(mc);

                mc.getOptions().add(option);
            } else {
                MultipleChoiceOption option = existingOptions.get(dto.id());

                if (option == null) {
                    throw new IllegalArgumentException("Invalid multiple choice option id: " + dto.id());
                }

                option.setOption(dto.option());
                option.setOrderIndex(i);
            }
        }

        multipleChoiceRepository.save(mc);

        return toQuestionResDto(mc);
    }

    @Override
    public MultipleChoiceResDto toQuestionResDto(MultipleChoice question) {
        var m = new MultipleChoiceResDto();

        populateCommonFields(question, m);

        m.setOptions(
                question.getOptions().stream()
                        .map(op ->
                                new MultipleChoiceResDto.MultipleChoiceOptionResDto(op.getId(), op.getOption(), op.getOrderIndex())
                        )
                        .toList()
        );

        return m;
    }

    @Override
    public boolean validateResponse(MultipleChoiceValidationRequestDto validationDto) {
        var mc = multipleChoiceRepository.findById(validationDto.getQuestionId())
                .orElseThrow(() -> new QuestionNotFoundException(QuestionType.MULTIPLE_CHOICE, validationDto.getQuestionId()));

        var present = mc.getOptions()
                .stream().anyMatch(op -> Objects.equals(op.getId(), validationDto.getResponseOptionId()));

        if (!present) {
            throw new ResponseValidationException(
                    "Invalid dropdown option ID: " + validationDto.getResponseOptionId()
            );
        }

        return true;
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.MULTIPLE_CHOICE;
    }

    @Override
    public void delete(Long questionId) {
        multipleChoiceRepository.deleteById(questionId);
    }

    private void setPropertiesForNew(MultipleChoiceAddUpdateReqDto source, MultipleChoice target, Question question) {
        var options = new ArrayList<MultipleChoiceOption>();

        for (int i = 0; i < source.getOptions().size(); i++) {
            var op = source.getOptions().get(i);
            var mcOp = new MultipleChoiceOption();

            mcOp.setOption(op.option());
            mcOp.setMultipleChoice(target);
            mcOp.setOrderIndex(i);

            options.add(mcOp);
        }

        target.setQuestion(question);
        target.setOptions(options);
    }
}
