package com.sougata.form_data_service.service.responseManager;

import com.sougata.form_data_service.constant.QuestionType;
import com.sougata.form_data_service.dto.question.request.LinearScaleResponseAddReqDto;
import com.sougata.form_data_service.dto.question.response.LinearScaleResDto;
import com.sougata.form_data_service.dto.response.individual.DropdownResponseIndividualDto;
import com.sougata.form_data_service.dto.response.individual.LinearScaleResponseIndividualDto;
import com.sougata.form_data_service.dto.response.question.LinearScaleResponseQuestionDto;
import com.sougata.form_data_service.dto.response.summary.LinearScaleResponseSummaryDto;
import com.sougata.form_data_service.feignClient.AuthServiceFeignClient;
import com.sougata.form_data_service.model.FormResponse;
import com.sougata.form_data_service.model.LinearScale;
import com.sougata.form_data_service.repository.FormResponseRepository;
import com.sougata.form_data_service.repository.LinearScaleRepository;
import com.sougata.form_data_service.repository.QuestionResponseRepository;
import com.sougata.form_data_service.util.IdUtil;
import jakarta.persistence.Tuple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service("LINEAR_SCALE_RESPONSE_MANAGER")
public class LinearScaleManager extends ResponseManager<
        LinearScaleResponseAddReqDto,
        LinearScaleResponseSummaryDto,
        LinearScaleResDto,
        LinearScaleResponseQuestionDto,
        LinearScaleResponseQuestionDto.Response,
        LinearScaleResponseQuestionDto.Summary,
        LinearScaleResponseIndividualDto
        > {

    private final LinearScaleRepository linearScaleRepository;

    @Autowired
    public LinearScaleManager(LinearScaleRepository linearScaleRepository, QuestionResponseRepository questionResponseRepository, FormResponseRepository formResponseRepository, AuthServiceFeignClient authServiceFeignClient) {
        super(questionResponseRepository);
        this.linearScaleRepository = linearScaleRepository;
    }

    @Override
    @Transactional
    public void create(LinearScaleResponseAddReqDto response, FormResponse formResponse) {
        LinearScale linearScale = new LinearScale();

        var qr = createQuestionResponse(response.getQuestionId(), formResponse);

        linearScale.setScale(response.getScale());
        linearScale.setQuestionResponse(qr);

        linearScaleRepository.save(linearScale);
    }

    @Override
    public List<LinearScaleResponseSummaryDto> getResponseSummaries(UUID formId, List<LinearScaleResDto> questionResponses) {
        var responseSummaries = linearScaleRepository.getResponseSummaries(formId);
        var result = new ArrayList<LinearScaleResponseSummaryDto>();

        var responseOptionCountMap = linearScaleRepository.getResponseScaleCount(formId)
                .stream().collect(Collectors.groupingBy(e -> e.get("questionId", Long.class)));

        questionResponses.forEach(qr ->
                result.add(
                        responseSummaries.stream()
                                .filter(rs -> Objects.equals(rs.questionId(), qr.getId()))
                                .map(rs -> {
                                    var ls = new LinearScaleResponseSummaryDto();

                                    ls.setQuestionId(qr.getId());
                                    ls.setQuestion(qr.getQuestion());
                                    ls.setOrderIndex(qr.getOrderIndex());
                                    ls.setNumberOfResponses(rs.numberOfResponses());
                                    ls.setQuestionType(QuestionType.LINEAR_SCALE);

                                    var countMap = new HashMap<Integer, Long>();

                                    responseOptionCountMap.get(qr.getId()).forEach(cm ->
                                            countMap.put(cm.get("scale", Integer.class), cm.get("responseCount", Long.class))
                                    );

                                    var scales = IntStream.rangeClosed(qr.getFromNumber(), qr.getToNumber()).boxed();

                                    var responses = scales.map(sc ->
                                            new LinearScaleResponseSummaryDto.Response(
                                                    sc,
                                                    countMap.getOrDefault(sc, 0L)

                                            )).toList();

                                    ls.setResponses(responses);

                                    return ls;
                                })
                                .findFirst()
                                .orElseGet(() -> {
                                    var ls = new LinearScaleResponseSummaryDto();

                                    ls.setQuestionId(qr.getId());
                                    ls.setQuestion(qr.getQuestion());
                                    ls.setOrderIndex(qr.getOrderIndex());
                                    ls.setNumberOfResponses(0L);
                                    ls.setQuestionType(QuestionType.LINEAR_SCALE);
                                    ls.setResponses(List.of());

                                    return ls;
                                })
                )
        );

        return result;
    }

    @Override
    public LinearScaleResponseSummaryDto getResponseSummary(UUID formId, Long questionId, LinearScaleResDto questionRes, Pageable pageable) {
        return null;
    }

    @Override
    public LinearScaleResponseQuestionDto.Summary getResponseByQuestionSummary(UUID formId, LinearScaleResDto questionResponse) {
        var sum = new LinearScaleResponseQuestionDto.Summary();

        sum.setQuestionId(questionResponse.getId());
        sum.setQuestion(questionResponse.getQuestion());
        sum.setQuestionType(questionResponse.getQuestionType());
        sum.setFromNumber(questionResponse.getFromNumber());
        sum.setToNumber(questionResponse.getToNumber());
        sum.setTotalResponseCount(getTotalResponseCount(formId, questionResponse.getId()));
        sum.setDistinctResponseCount(linearScaleRepository.getDistinctResponseCount(formId, questionResponse.getId()));

        return sum;
    }

    @Override
    public LinearScaleResponseQuestionDto getResponseByQuestion(UUID formId, Long questionId, Map<String, String> extraParams, Pageable pageable) {
        var grouped = linearScaleRepository.groupedByResponseScale(formId, questionId, pageable);

        var ls = new LinearScaleResponseQuestionDto();

        var responses = grouped.stream().map(g -> {
            var res = new LinearScaleResponseQuestionDto.Response();

            res.setQuestionId(questionId);
            res.setQuestionType(getQuestionType());
            res.setScale(g.get("scale", Integer.class));
            res.setResponseCount(g.get("responseCount", Long.class));

            var map = new HashMap<String, List<String>>();

            map.put("scale", List.of(res.getScale() == null ? "" : res.getScale().toString()));

            res.setFormResponsesIdentifier(IdUtil.generateCompressedEncodedId(map));

            return res;
        }).toList();


        ls.setQuestionId(questionId);
        ls.setQuestionType(getQuestionType());
        ls.setResponses(responses);

        return ls;
    }

    @Override
    public List<LinearScaleResponseIndividualDto> getIndividualResponses(UUID formId, Long formResponseId) {
        var responses = linearScaleRepository.getScalesByFormResponse(formId, formResponseId);

        return responses.stream().map(tuple -> {
            var qId = tuple.get("questionId", Long.class);
            var scale = tuple.get("scale", Integer.class);

            var res = new LinearScaleResponseIndividualDto();

            res.setQuestionId(qId);
            res.setQuestionType(getQuestionType());
            res.setScale(scale);

            return res;
        }).toList();
    }

    @Override
    public List<Tuple> getFormResponseAndUserIds(UUID formId, Long questionId, String formResponsesIdentifier, Pageable pageable) {
        return List.of();
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.LINEAR_SCALE;
    }

    @Override
    public void deleteResponses(UUID formId, Long questionId) {
        linearScaleRepository.deleteAllByFormIdAndQuestionId(formId, questionId);
    }
}
