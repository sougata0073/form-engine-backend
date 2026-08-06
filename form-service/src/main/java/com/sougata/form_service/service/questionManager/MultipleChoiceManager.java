package com.sougata.form_service.service.questionManager;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.question.request.MultipleChoiceAddUpdateReqDto;
import com.sougata.form_service.dto.question.response.MultipleChoiceResDto;
import com.sougata.form_service.exception.QuestionNotFoundException;
import com.sougata.form_service.model.questionSchema.MultipleChoice;
import com.sougata.form_service.model.questionSchema.MultipleChoiceOption;
import com.sougata.form_service.model.questionSchema.Question;
import com.sougata.form_service.repository.MultipleChoiceOptionRepository;
import com.sougata.form_service.repository.MultipleChoiceRepository;
import com.sougata.form_service.repository.QuestionRepository;
import com.sougata.form_service.service.FormService;
import com.sougata.form_service.service.QuestionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service("MULTIPLE_CHOICE_QUESTION_MANAGER")
public class MultipleChoiceManager extends QuestionManager<MultipleChoice, MultipleChoiceAddUpdateReqDto, MultipleChoiceResDto> {

    private final MultipleChoiceRepository multipleChoiceRepository;
    private final MultipleChoiceOptionRepository multipleChoiceOptionRepository;

    public MultipleChoiceManager(MultipleChoiceRepository multipleChoiceRepository, FormService formService, QuestionRepository questionRepository, MultipleChoiceOptionRepository multipleChoiceOptionRepository) {
        super(questionRepository, formService);
        this.multipleChoiceRepository = multipleChoiceRepository;
        this.multipleChoiceOptionRepository = multipleChoiceOptionRepository;
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

        var question = updateQuestion(formId, questionId, crudDto);

        setPropertiesForNew(crudDto, newMc, question);

        var saved = multipleChoiceRepository.save(newMc);

        return toQuestionResDto(saved);
    }

    @Override
    @Transactional
    public MultipleChoiceResDto update(UUID formId, Long questionId, MultipleChoiceAddUpdateReqDto crudDto) {
        MultipleChoice mc = multipleChoiceRepository.findByQuestion_FormIdAndQuestion_Id(formId, questionId)
                .orElseThrow(() -> new QuestionNotFoundException(QuestionType.MULTIPLE_CHOICE, questionId));

        updateQuestion(formId, questionId, crudDto);

        Map<Long, MultipleChoiceOption> existingOptions = mc.getOptions().stream()
                .collect(Collectors.toMap(MultipleChoiceOption::getId, option -> option));

        Set<Long> requestOptionIds = crudDto.getOptions().stream()
                .map(MultipleChoiceAddUpdateReqDto.Option::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        mc.getOptions().removeIf(option -> !requestOptionIds.contains(option.getId()));

        for (int i = 0; i < crudDto.getOptions().size(); i++) {
            var dto = crudDto.getOptions().get(i);

            if (dto.getId() == null) {
                MultipleChoiceOption option = new MultipleChoiceOption();
                option.setOption(dto.getOption());
                option.setOrderIndex(i);
                option.setMultipleChoice(mc);

                mc.getOptions().add(option);
            } else {
                MultipleChoiceOption option = existingOptions.get(dto.getId());

                if (option == null) {
                    throw new IllegalArgumentException("Invalid multiple choice option id: " + dto.getId());
                }

                option.setOption(dto.getOption());
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
    public QuestionType getQuestionType() {
        return QuestionType.MULTIPLE_CHOICE;
    }

    @Override
    @Transactional
    public void delete(UUID formId, Long questionId) {
        multipleChoiceOptionRepository.deleteAllByFormIdAndMultipleChoiceId(formId, questionId);
        multipleChoiceRepository.deleteQuestion(formId, questionId);
    }

    private void setPropertiesForNew(MultipleChoiceAddUpdateReqDto source, MultipleChoice target, Question question) {
        var options = new ArrayList<MultipleChoiceOption>();

        for (int i = 0; i < source.getOptions().size(); i++) {
            var op = source.getOptions().get(i);
            var mcOp = new MultipleChoiceOption();

            mcOp.setOption(op.getOption());
            mcOp.setMultipleChoice(target);
            mcOp.setOrderIndex(i);

            options.add(mcOp);
        }

        target.setQuestion(question);
        target.setOptions(options);
    }
}
