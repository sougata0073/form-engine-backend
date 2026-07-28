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
import com.sougata.form_data_service.repository.QuestionResponseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service("CHECKBOX_RESPONSE_MANAGER")
public class CheckboxManager extends ResponseManager<
        CheckboxResponseAddReqDto,
        CheckboxResponseSummaryDto,
        CheckboxResDto,
        CheckboxResponseQuestionDto,
        CheckboxResponseQuestionDto.Response,
        CheckboxResponseQuestionDto.Summary
        > {

    private final CheckboxRepository checkboxRepository;

    @Autowired
    public CheckboxManager(CheckboxRepository checkboxRepository, QuestionResponseRepository questionResponseRepository) {
        super(questionResponseRepository);
        this.checkboxRepository = checkboxRepository;
    }

    @Override
    @Transactional
    public void create(CheckboxResponseAddReqDto response, FormResponse formResponse) {
        Checkbox cb = new Checkbox();

        var qr = createQuestionResponse(response.getQuestionId(), formResponse);

        var responses = response.getResponseOptionIds().stream().map(id -> {
            var op = new CheckboxOption();

            op.setResponseOptionId(id);
            op.setCheckbox(cb);

            return op;
        }).collect(Collectors.toCollection(ArrayList::new));

        cb.setResponses(responses);
        cb.setQuestionResponse(qr);

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
    public CheckboxResponseQuestionDto.Summary getResponseByQuestionSummary(UUID formId, CheckboxResDto questionResponse) {
        var sum = new CheckboxResponseQuestionDto.Summary();

        sum.setQuestionId(questionResponse.getId());
        sum.setQuestion(questionResponse.getQuestion());
        sum.setQuestionType(questionResponse.getQuestionType());
        sum.setOptions(questionResponse.getOptions());
        sum.setTotalResponseCount(getTotalResponseCount(formId, questionResponse.getId()));
        sum.setDistinctResponseCount(checkboxRepository.getDistinctResponseCount(formId, questionResponse.getId()));

        return sum;
    }

    @Override
    public CheckboxResponseQuestionDto getResponseByQuestion(UUID formId, Long questionId, Map<String, String> extraParams, Pageable pageable) {
        var grouped = checkboxRepository.groupedByResponseOptions(formId, questionId, pageable);

        var cb = new CheckboxResponseQuestionDto();

        var responses = grouped.stream().map(g -> {
            var res = new CheckboxResponseQuestionDto.Response();

            res.setOptionIds(Arrays.stream(g.get("optionIds", Long[].class)).map(Object::toString).toList());
            res.setResponseCount(g.get("responseCount", Long.class));
            res.setResponseIds(Arrays.stream(g.get("responseIds", Long[].class)).map(Object::toString).toList());

            return res;
        }).toList();

        cb.setResponses(responses);

        return cb;
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.CHECKBOX;
    }

    @Override
    public void deleteResponses(UUID formId, Long questionId) {
        checkboxRepository.deleteAllByFormIdAndQuestionId(formId, questionId);
    }
}
