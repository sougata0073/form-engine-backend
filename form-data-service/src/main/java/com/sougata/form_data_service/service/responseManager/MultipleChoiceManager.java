package com.sougata.form_data_service.service.responseManager;

import com.sougata.form_data_service.constant.QuestionType;
import com.sougata.form_data_service.dto.question.request.MultipleChoiceResponseAddReqDto;
import com.sougata.form_data_service.dto.question.response.MultipleChoiceResDto;
import com.sougata.form_data_service.dto.response.question.MultipleChoiceResponseQuestionDto;
import com.sougata.form_data_service.dto.response.summary.MultipleChoiceResponseSummaryDto;
import com.sougata.form_data_service.model.FormResponse;
import com.sougata.form_data_service.model.MultipleChoice;
import com.sougata.form_data_service.repository.MultipleChoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service("MULTIPLE_CHOICE_RESPONSE_MANAGER")
public class MultipleChoiceManager extends ResponseManager<MultipleChoiceResponseAddReqDto, MultipleChoiceResponseSummaryDto, MultipleChoiceResDto, MultipleChoiceResponseQuestionDto> {

    private final MultipleChoiceRepository multipleChoiceRepository;

    @Autowired
    public MultipleChoiceManager(MultipleChoiceRepository multipleChoiceRepository) {
        this.multipleChoiceRepository = multipleChoiceRepository;
    }

    @Override
    public void create(MultipleChoiceResponseAddReqDto response, FormResponse formResponse) {
        MultipleChoice multipleChoice = new MultipleChoice();
        multipleChoice.setResponseOptionId(response.getResponseOptionId());
        multipleChoice.setQuestionId(response.getQuestionId());
        multipleChoice.setFormResponse(formResponse);

        multipleChoiceRepository.save(multipleChoice);
    }

    @Override
    public List<MultipleChoiceResponseSummaryDto> getResponseSummaries(UUID formId, List<MultipleChoiceResDto> questionResponses) {
        var responseSummaries = multipleChoiceRepository.getResponseSummaries(formId);
        var result = new ArrayList<MultipleChoiceResponseSummaryDto>();

        var responseOptionCountMap = multipleChoiceRepository.getResponseOptionCount(formId)
                .stream().collect(Collectors.groupingBy(e -> e.get("questionId", Long.class)));

        questionResponses.forEach(qr ->
                result.add(
                        responseSummaries.stream()
                                .filter(rs -> Objects.equals(rs.questionId(), qr.getId()))
                                .map(rs -> {
                                    var dd = new MultipleChoiceResponseSummaryDto();

                                    dd.setQuestionId(qr.getId());
                                    dd.setQuestion(qr.getQuestion());
                                    dd.setOrderIndex(qr.getOrderIndex());
                                    dd.setNumberOfResponses(rs.numberOfResponses());
                                    dd.setQuestionType(QuestionType.MULTIPLE_CHOICE);

                                    var countMap = new HashMap<Long, Long>();

                                    responseOptionCountMap.get(qr.getId()).forEach(cm ->
                                            countMap.put(cm.get("responseOptionId", Long.class), cm.get("responseCount", Long.class))
                                    );

                                    var responses = qr.getOptions().stream().map(op ->
                                            new MultipleChoiceResponseSummaryDto.Response(
                                                    op.id(),
                                                    op.option(),
                                                    countMap.getOrDefault(op.id(), 0L)

                                            )).toList();

                                    dd.setResponses(responses);

                                    return dd;
                                })
                                .findFirst()
                                .orElseGet(() -> {
                                    var dd = new MultipleChoiceResponseSummaryDto();

                                    dd.setQuestionId(qr.getId());
                                    dd.setQuestion(qr.getQuestion());
                                    dd.setOrderIndex(qr.getOrderIndex());
                                    dd.setNumberOfResponses(0L);
                                    dd.setQuestionType(QuestionType.MULTIPLE_CHOICE);
                                    dd.setResponses(List.of());

                                    return dd;
                                })
                )
        );

        return result;
    }

    @Override
    public MultipleChoiceResponseQuestionDto getResponseByQuestion(UUID formId, MultipleChoiceResDto questionRes) {
        return null;
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.MULTIPLE_CHOICE;
    }

    @Override
    public void deleteResponses(UUID formId, Long questionId) {
        var entities = multipleChoiceRepository.findByFormIdAndQuestionId(formId, questionId);

        multipleChoiceRepository.deleteAll(entities);
    }
}
