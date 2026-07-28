package com.sougata.form_data_service.service.responseManager;

import com.sougata.form_data_service.constant.QuestionType;
import com.sougata.form_data_service.dto.question.request.MultipleChoiceResponseAddReqDto;
import com.sougata.form_data_service.dto.question.response.MultipleChoiceResDto;
import com.sougata.form_data_service.dto.response.question.MultipleChoiceResponseQuestionDto;
import com.sougata.form_data_service.dto.response.summary.MultipleChoiceResponseSummaryDto;
import com.sougata.form_data_service.model.FormResponse;
import com.sougata.form_data_service.model.MultipleChoice;
import com.sougata.form_data_service.repository.MultipleChoiceRepository;
import com.sougata.form_data_service.repository.QuestionResponseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service("MULTIPLE_CHOICE_RESPONSE_MANAGER")
public class MultipleChoiceManager extends ResponseManager<
        MultipleChoiceResponseAddReqDto,
        MultipleChoiceResponseSummaryDto,
        MultipleChoiceResDto,
        MultipleChoiceResponseQuestionDto,
        MultipleChoiceResponseQuestionDto.Response,
        MultipleChoiceResponseQuestionDto.Summary
        > {

    private final MultipleChoiceRepository multipleChoiceRepository;

    @Autowired
    public MultipleChoiceManager(MultipleChoiceRepository multipleChoiceRepository, QuestionResponseRepository questionResponseRepository) {
        super(questionResponseRepository);
        this.multipleChoiceRepository = multipleChoiceRepository;
    }

    @Override
    @Transactional
    public void create(MultipleChoiceResponseAddReqDto response, FormResponse formResponse) {
        MultipleChoice multipleChoice = new MultipleChoice();

        var qr = createQuestionResponse(response.getQuestionId(), formResponse);

        multipleChoice.setResponseOptionId(response.getResponseOptionId());
        multipleChoice.setQuestionResponse(qr);

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
    public MultipleChoiceResponseQuestionDto.Summary getResponseByQuestionSummary(UUID formId, MultipleChoiceResDto questionResponse) {
        var sum = new MultipleChoiceResponseQuestionDto.Summary();

        sum.setQuestionId(questionResponse.getId());
        sum.setQuestion(questionResponse.getQuestion());
        sum.setQuestionType(questionResponse.getQuestionType());
        sum.setOptions(questionResponse.getOptions());
        sum.setTotalResponseCount(getTotalResponseCount(formId, questionResponse.getId()));
        sum.setDistinctResponseCount(multipleChoiceRepository.getDistinctResponseCount(formId, questionResponse.getId()));

        return sum;
    }

    @Override
    public MultipleChoiceResponseQuestionDto getResponseByQuestion(UUID formId, Long questionId, Map<String, String> extraParams, Pageable pageable) {
        var grouped = multipleChoiceRepository.groupedByResponseOption(formId, questionId, pageable);

        var mc = new MultipleChoiceResponseQuestionDto();

        var responses = grouped.stream().map(g -> {
            var res = new MultipleChoiceResponseQuestionDto.Response();

            res.setOptionId(g.get("optionId", Long.class));
            res.setResponseCount(g.get("responseCount", Long.class));
            res.setResponseIds(Arrays.stream(g.get("responseIds", Long[].class)).map(Object::toString).toList());

            return res;
        }).toList();

        mc.setResponses(responses);

        return mc;
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.MULTIPLE_CHOICE;
    }

    @Override
    public void deleteResponses(UUID formId, Long questionId) {
        multipleChoiceRepository.deleteAllByFormIdAndQuestionId(formId, questionId);
    }
}
