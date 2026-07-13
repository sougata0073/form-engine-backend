package com.sougata.form_service.service.questionManager;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.question.request.MultipleChoiceAddUpdateReqDto;
import com.sougata.form_service.dto.question.response.MultipleChoiceResDto;
import com.sougata.form_service.dto.validation.request.MultipleChoiceValidationRequestDto;
import com.sougata.form_service.exception.QuestionNotFoundException;
import com.sougata.form_service.exception.ResponseValidationException;
import com.sougata.form_service.model.questionSchema.MultipleChoice;
import com.sougata.form_service.model.questionSchema.MultipleChoiceOption;
import com.sougata.form_service.repository.MultipleChoiceRepository;
import com.sougata.form_service.service.FormService;
import com.sougata.form_service.service.QuestionManager;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service("MULTIPLE_CHOICE_QUESTION_MANAGER")
public class MultipleChoiceManager extends QuestionManager<MultipleChoiceAddUpdateReqDto, MultipleChoiceResDto, MultipleChoiceValidationRequestDto> {

    private final MultipleChoiceRepository multipleChoiceRepository;
    private final FormService formService;

    public MultipleChoiceManager(MultipleChoiceRepository multipleChoiceRepository, FormService formService) {
        this.multipleChoiceRepository = multipleChoiceRepository;
        this.formService = formService;
    }

    @Override
    public MultipleChoiceResDto get(UUID formId, Long questionId) {
        return MultipleChoiceResDto.create(multipleChoiceRepository.findByFormIdAndId(formId, questionId).orElseThrow(() -> new QuestionNotFoundException(questionId)));
    }

    @Override
    public MultipleChoiceResDto create(UUID formId, MultipleChoiceAddUpdateReqDto crudDto) {
        MultipleChoice newMc = new MultipleChoice();

        setProperties(crudDto, formId, newMc);

        MultipleChoice saved = multipleChoiceRepository.save(newMc);

        return MultipleChoiceResDto.create(saved);
    }

    @Override
    public MultipleChoiceResDto create(UUID formId, Long questionId, MultipleChoiceAddUpdateReqDto crudDto) {
        MultipleChoice newMc = new MultipleChoice();

        newMc.setId(questionId);
        setProperties(crudDto, formId, newMc);

        MultipleChoice saved = multipleChoiceRepository.save(newMc);

        return MultipleChoiceResDto.create(saved);
    }

    @Override
    public MultipleChoiceResDto update(Long questionId, MultipleChoiceAddUpdateReqDto crudDto) {
        MultipleChoice mc = multipleChoiceRepository.findById(questionId)
                .orElseThrow(() -> new QuestionNotFoundException(QuestionType.MULTIPLE_CHOICE, questionId));
        setProperties(crudDto, mc);
        multipleChoiceRepository.save(mc);

        return MultipleChoiceResDto.create(mc);
    }

    @Override
    public boolean exists(Long questionId) {
        return multipleChoiceRepository.existsById(questionId);
    }

    @Override
    public void delete(Long questionId) {
        multipleChoiceRepository.deleteById(questionId);
    }

    @Override
    public boolean validateResponse(MultipleChoiceValidationRequestDto validationDto) {
        var mc = multipleChoiceRepository.findById(validationDto.getQuestionId())
                .orElseThrow(() -> new QuestionNotFoundException(QuestionType.MULTIPLE_CHOICE, validationDto.getQuestionId()));

        var present = mc.getOptions()
                .stream().anyMatch(op -> Objects.equals(op.getId(), validationDto.getResponseOptionId()));

        if(!present) {
            throw new ResponseValidationException(
                    "Invalid dropdown option ID: " + validationDto.getResponseOptionId()
            );
        }

        return true;
    }

    @Override
    public Class<MultipleChoiceAddUpdateReqDto> getCrudDtoClass() {
        return MultipleChoiceAddUpdateReqDto.class;
    }

    @Override
    public Class<MultipleChoiceValidationRequestDto> getValidationDtoClass() {
        return MultipleChoiceValidationRequestDto.class;
    }

    @Override
    @SuppressWarnings("unchecked")
    public MultipleChoiceRepository getQuestionRepository() {
        return multipleChoiceRepository;
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.MULTIPLE_CHOICE;
    }

    private void setProperties(MultipleChoiceAddUpdateReqDto source, UUID formId, MultipleChoice target) {

        target.setQuestion(source.getQuestion());
        target.setDescription(source.getDescription());
        target.setRequired(source.getRequired());
        target.setOrderIndex(source.getOrderIndex());

        Map<Long, MultipleChoiceOption> existingOptions = target.getOptions().stream()
                .collect(Collectors.toMap(MultipleChoiceOption::getId, option -> option));

        Set<Long> requestOptionIds = source.getOptions().stream()
                .map(MultipleChoiceAddUpdateReqDto.Option::id)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        target.getOptions().removeIf(option -> !requestOptionIds.contains(option.getId()));

        for (int i = 0; i < source.getOptions().size(); i++) {

            var dto = source.getOptions().get(i);

            if (dto.id() == null) {

                MultipleChoiceOption option = new MultipleChoiceOption();
                option.setOption(dto.option());
                option.setOrderIndex(i);
                option.setMultipleChoice(target);

                target.getOptions().add(option);

            } else {

                MultipleChoiceOption option = existingOptions.get(dto.id());

                if (option == null) {
                    throw new IllegalArgumentException("Invalid multiple choice option id: " + dto.id());
                }

                option.setOption(dto.option());
                option.setOrderIndex(i);
            }
        }

        if (formId != null) {
            target.setForm(formService.getFormById(formId));
        }
    }

    private void setProperties(MultipleChoiceAddUpdateReqDto source, MultipleChoice target) {
        setProperties(source, null, target);
    }
}
