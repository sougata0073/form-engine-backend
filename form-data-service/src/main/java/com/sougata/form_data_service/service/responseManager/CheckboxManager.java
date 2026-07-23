package com.sougata.form_data_service.service.responseManager;

import com.sougata.form_data_service.constant.QuestionType;
import com.sougata.form_data_service.dto.question.request.CheckboxResponseAddReqDto;
import com.sougata.form_data_service.dto.question.response.CheckboxResDto;
import com.sougata.form_data_service.dto.response.question.CheckboxResponseQuestionDto;
import com.sougata.form_data_service.dto.response.summary.CheckboxResponseSummaryDto;
import com.sougata.form_data_service.model.Checkbox;
import com.sougata.form_data_service.model.CheckboxOption;
import com.sougata.form_data_service.model.FormResponse;
import com.sougata.form_data_service.repository.CheckboxRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service("CHECKBOX_RESPONSE_MANAGER")
public class CheckboxManager extends ResponseManager<CheckboxResponseAddReqDto, CheckboxResponseSummaryDto, CheckboxResDto, CheckboxResponseQuestionDto> {

    private final CheckboxRepository checkboxRepository;

    @Autowired
    public CheckboxManager(CheckboxRepository checkboxRepository) {
        this.checkboxRepository = checkboxRepository;
    }

    @Override
    public void create(CheckboxResponseAddReqDto response, FormResponse formResponse) {
        Checkbox cb = new Checkbox();
        cb.setQuestionId(response.getQuestionId());
        cb.setFormResponse(formResponse);

        var responses = response.getResponseOptionIds().stream().map(id -> {
            var op = new CheckboxOption();

            op.setResponseOptionId(id);
            op.setCheckbox(cb);

            return op;
        }).collect(Collectors.toCollection(ArrayList::new));

        cb.setResponses(responses);

        checkboxRepository.save(cb);
    }

    @Override
    public List<CheckboxResponseSummaryDto> getResponseSummaries(UUID formId, List<CheckboxResDto> questionResponses) {

        var responseSummaries = checkboxRepository.getResponseSummaries(formId);
        var result = new ArrayList<CheckboxResponseSummaryDto>();

        var responseOptionCountMap = checkboxRepository.getResponseOptionCount(formId)
                .stream().collect(Collectors.groupingBy(e -> e.get("questionId", Long.class)));

        questionResponses.forEach(qr ->
                result.add(
                        responseSummaries.stream()
                                .filter(rs -> Objects.equals(rs.questionId(), qr.getId()))
                                .map(rs -> {
                                    var cb = new CheckboxResponseSummaryDto();

                                    cb.setQuestionId(qr.getId());
                                    cb.setQuestion(qr.getQuestion());
                                    cb.setOrderIndex(qr.getOrderIndex());
                                    cb.setNumberOfResponses(rs.numberOfResponses());
                                    cb.setQuestionType(QuestionType.CHECKBOX);

                                    var countMap = new HashMap<Long, Long>();

                                    responseOptionCountMap.get(qr.getId()).forEach(cm ->
                                            countMap.put(cm.get("responseOptionId", Long.class), cm.get("responseCount", Long.class))
                                    );

                                    var responses = qr.getOptions().stream().map(op ->
                                            new CheckboxResponseSummaryDto.Response(
                                                    op.id(),
                                                    op.option(),
                                                    countMap.getOrDefault(op.id(), 0L)

                                            )).toList();

                                    cb.setResponses(responses);

                                    return cb;
                                })
                                .findFirst()
                                .orElseGet(() -> {
                                    var cb = new CheckboxResponseSummaryDto();

                                    cb.setQuestionId(qr.getId());
                                    cb.setQuestion(qr.getQuestion());
                                    cb.setOrderIndex(qr.getOrderIndex());
                                    cb.setNumberOfResponses(0L);
                                    cb.setQuestionType(QuestionType.CHECKBOX);
                                    cb.setResponses(List.of());

                                    return cb;
                                })
                )
        );

        return result;
    }

    @Override
    public CheckboxResponseQuestionDto getResponseByQuestion(UUID formId, CheckboxResDto questionRes) {
        var grouped = checkboxRepository.groupedByResponseOptions(formId, questionRes.getId());

        var cb = new CheckboxResponseQuestionDto();

        var responses = grouped.stream().map(g -> new CheckboxResponseQuestionDto.Response(
                Arrays.stream(g.get("optionIds", Long[].class)).map(Object::toString).toList(),
                g.get("responseCount", Long.class).intValue(),
                Arrays.stream(g.get("responseIds", Long[].class)).map(Object::toString).toList()
        )).toList();

        cb.setOptions(questionRes.getOptions());
        cb.setQuestionId(questionRes.getId());
        cb.setQuestion(questionRes.getQuestion());
        cb.setQuestionType(questionRes.getQuestionType());
        cb.setResponses(responses);

        return cb;
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.CHECKBOX;
    }

    @Override
    public void deleteResponses(UUID formId, Long questionId) {
        var entities = checkboxRepository.findByFormIdAndQuestionId(formId, questionId);

        checkboxRepository.deleteAll(entities);
    }
}
