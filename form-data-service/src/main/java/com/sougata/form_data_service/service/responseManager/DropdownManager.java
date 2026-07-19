package com.sougata.form_data_service.service.responseManager;

import com.sougata.form_data_service.constant.QuestionType;
import com.sougata.form_data_service.dto.question.request.DropdownResponseAddReqDto;
import com.sougata.form_data_service.dto.response.question.DropdownResponseQuestionDto;
import com.sougata.form_data_service.dto.response.summary.DropdownResponseSummaryDto;
import com.sougata.form_data_service.form_schema.dto.questionSchema.response.DropdownResDto;
import com.sougata.form_data_service.model.Dropdown;
import com.sougata.form_data_service.model.FormResponse;
import com.sougata.form_data_service.repository.DropdownRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service("DROPDOWN_RESPONSE_MANAGER")
public class DropdownManager extends ResponseManager<DropdownResponseAddReqDto, DropdownResponseSummaryDto, DropdownResDto, DropdownResponseQuestionDto> {

    private final DropdownRepository dropdownRepository;

    @Autowired
    public DropdownManager(DropdownRepository dropdownRepository) {
        this.dropdownRepository = dropdownRepository;
    }

    @Override
    public void create(DropdownResponseAddReqDto response, FormResponse formResponse) {
        Dropdown dropdown = new Dropdown();
        dropdown.setResponseOptionId(response.getResponseOptionId());
        dropdown.setQuestionId(response.getQuestionId());
        dropdown.setFormResponse(formResponse);

        dropdownRepository.save(dropdown);
    }

    @Override
    public List<DropdownResponseSummaryDto> getResponseSummaries(UUID formId, List<DropdownResDto> questionResponses) {
        var responseSummaries = dropdownRepository.getResponseSummaries(formId);
        var result = new ArrayList<DropdownResponseSummaryDto>();

        var responseOptionCountMap = dropdownRepository.getResponseOptionCount(formId)
                .stream().collect(Collectors.groupingBy(e -> e.get("questionId", Long.class)));

        questionResponses.forEach(qr ->
                result.add(
                        responseSummaries.stream()
                                .filter(rs -> Objects.equals(rs.questionId(), qr.getId()))
                                .map(rs -> {
                                    var dd = new DropdownResponseSummaryDto();

                                    dd.setQuestionId(qr.getId());
                                    dd.setQuestion(qr.getQuestion());
                                    dd.setOrderIndex(qr.getOrderIndex());
                                    dd.setNumberOfResponses(rs.numberOfResponses());
                                    dd.setQuestionType(QuestionType.DROPDOWN);

                                    var countMap = new HashMap<Long, Long>();

                                    responseOptionCountMap.get(qr.getId()).forEach(cm ->
                                            countMap.put(cm.get("responseOptionId", Long.class), cm.get("responseCount", Long.class))
                                    );

                                    var responses = qr.getOptions().stream().map(op ->
                                            new DropdownResponseSummaryDto.Response(
                                                    op.id(),
                                                    op.option(),
                                                    countMap.getOrDefault(op.id(), 0L)

                                            )).toList();

                                    dd.setResponses(responses);

                                    return dd;
                                })
                                .findFirst()
                                .orElseGet(() -> {
                                    var dd = new DropdownResponseSummaryDto();

                                    dd.setQuestionId(qr.getId());
                                    dd.setQuestion(qr.getQuestion());
                                    dd.setOrderIndex(qr.getOrderIndex());
                                    dd.setNumberOfResponses(0L);
                                    dd.setQuestionType(QuestionType.DROPDOWN);
                                    dd.setResponses(List.of());

                                    return dd;
                                })
                )
        );

        return result;
    }

    @Override
    public DropdownResponseQuestionDto getResponseByQuestion(UUID formId, DropdownResDto questionRes) {
        var grouped = dropdownRepository.groupedByResponseOption(formId, questionRes.getId());

        var d = new DropdownResponseQuestionDto();

        var responses = grouped.stream().map(g -> new DropdownResponseQuestionDto.Response(
                g.get("optionId", Long.class),
                g.get("responseCount", Long.class).intValue(),
                Arrays.stream(g.get("responseIds", Long[].class)).map(Object::toString).toList()
        )).toList();

        d.setOptions(questionRes.getOptions());
        d.setQuestionId(questionRes.getId());
        d.setQuestion(questionRes.getQuestion());
        d.setQuestionType(questionRes.getQuestionType());
        d.setResponses(responses);

        return d;
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.DROPDOWN;
    }

    @Override
    public void deleteResponses(UUID formId, Long questionId) {
        var entities = dropdownRepository.findByFormIdAndQuestionId(formId, questionId);

        dropdownRepository.deleteAll(entities);
    }
}
