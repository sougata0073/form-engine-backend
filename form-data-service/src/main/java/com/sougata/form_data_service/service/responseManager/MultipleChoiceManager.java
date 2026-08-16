package com.sougata.form_data_service.service.responseManager;

import com.sougata.form_data_service.constant.QuestionType;
import com.sougata.form_data_service.dto.question.request.MultipleChoiceResponseAddReqDto;
import com.sougata.form_data_service.dto.question.response.MultipleChoiceResDto;
import com.sougata.form_data_service.dto.response.individual.MultipleChoiceResponseIndividualDto;
import com.sougata.form_data_service.dto.response.question.MultipleChoiceResponseQuestionDto;
import com.sougata.form_data_service.dto.response.summary.MultipleChoiceResponseSummaryDto;
import com.sougata.form_data_service.feignClient.AuthServiceFeignClient;
import com.sougata.form_data_service.model.FormResponse;
import com.sougata.form_data_service.model.MultipleChoice;
import com.sougata.form_data_service.repository.FormResponseRepository;
import com.sougata.form_data_service.repository.MultipleChoiceRepository;
import com.sougata.form_data_service.repository.QuestionResponseRepository;
import com.sougata.form_data_service.util.IdUtil;
import jakarta.persistence.Tuple;
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
        MultipleChoiceResponseQuestionDto.Summary,
        MultipleChoiceResponseIndividualDto
        > {

    private final MultipleChoiceRepository multipleChoiceRepository;

    @Autowired
    public MultipleChoiceManager(MultipleChoiceRepository multipleChoiceRepository, QuestionResponseRepository questionResponseRepository, FormResponseRepository formResponseRepository, AuthServiceFeignClient authServiceFeignClient) {
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
    public MultipleChoiceResponseSummaryDto getResponseSummary(UUID formId, Long questionId, MultipleChoiceResDto questionRes, Pageable pageable) {
        var responseSummary = multipleChoiceRepository.getResponseSummary(formId, questionId);
        var res = new MultipleChoiceResponseSummaryDto();

        res.setQuestionId(questionRes.getId());
        res.setQuestion(questionRes.getQuestion());
        res.setQuestionType(getQuestionType());
        res.setOrderIndex(questionRes.getOrderIndex());
        res.setNumberOfResponses(responseSummary.numberOfResponses());
        res.setResponses(List.of());

        return res;
    }

    @Override
    public MultipleChoiceResponseQuestionDto.Summary getResponseByQuestionSummary(UUID formId, MultipleChoiceResDto questionResponse) {
        var sum = new MultipleChoiceResponseQuestionDto.Summary();

        sum.setOptions(questionResponse.getOptions());

        return sum;
    }

    @Override
    public MultipleChoiceResponseQuestionDto getResponseByQuestion(UUID formId, Long questionId, Map<String, String> extraParams, Pageable pageable) {
        var grouped = multipleChoiceRepository.groupedByResponseOption(formId, questionId, pageable);

        var mc = new MultipleChoiceResponseQuestionDto();

        var responses = grouped.stream().map(g -> {
            var res = new MultipleChoiceResponseQuestionDto.Response();

            res.setQuestionId(questionId);
            res.setQuestionType(getQuestionType());
            res.setOptionId(g.get("optionId", Long.class));
            res.setResponseCount(g.get("responseCount", Long.class));

            var map = new HashMap<String, List<String>>();

            map.put("optionId", List.of(res.getOptionId() == null ? "" : res.getOptionId().toString()));

            res.setFormResponsesIdentifier(IdUtil.generateCompressedEncodedId(map));

            return res;
        }).toList();


        mc.setQuestionId(questionId);
        mc.setQuestionType(getQuestionType());
        mc.setResponses(responses);

        return mc;
    }

    @Override
    public List<MultipleChoiceResponseIndividualDto> getIndividualResponses(UUID formId, Long formResponseId) {
        var responses = multipleChoiceRepository.getOptionIdsByFormResponse(formId, formResponseId);

        return responses.stream().map(tuple -> {
            var qId = tuple.get("questionId", Long.class);
            var optionId = tuple.get("optionId", Long.class);

            var res = new MultipleChoiceResponseIndividualDto();

            res.setQuestionId(qId);
            res.setQuestionType(getQuestionType());
            res.setOptionId(optionId);

            return res;
        }).toList();
    }

    @Override
    public List<Tuple> getFormResponseAndUserIds(UUID formId, Long questionId, String formResponsesIdentifier, Pageable pageable) {
        var map = IdUtil.reconstructCompressedEncodedId(formResponsesIdentifier);

        var optionId = map.get("optionId");

        if (optionId.isEmpty()) {
            throw new IllegalArgumentException("Invalid Form Responses Identifier. Identifier: " + formResponsesIdentifier);
        }

        var groupedResponse = optionId.getFirst() == null ? null : Long.parseLong(optionId.getFirst());

        return multipleChoiceRepository.getResponseIdsByGroupedResponse(formId, questionId, groupedResponse, pageable);
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.MULTIPLE_CHOICE;
    }

    @Override
    public void deleteResponsesByQuestion(UUID formId, Long questionId) {
        multipleChoiceRepository.deleteAllByFormIdAndQuestionId(formId, questionId);
    }

    @Override
    public void deleteResponsesByFormResponse(UUID formId, Long formResponseId) {
        multipleChoiceRepository.deleteAllByFormIdAndFormResponseId(formId, formResponseId);
    }
}
