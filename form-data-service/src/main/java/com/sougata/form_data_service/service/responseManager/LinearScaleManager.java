package com.sougata.form_data_service.service.responseManager;

import com.sougata.form_data_service.constant.QuestionType;
import com.sougata.form_data_service.dto.question.request.LinearScaleResponseAddReqDto;
import com.sougata.form_data_service.dto.question.response.LinearScaleResDto;
import com.sougata.form_data_service.dto.response.question.LinearScaleResponseQuestionDto;
import com.sougata.form_data_service.dto.response.summary.LinearScaleResponseSummaryDto;
import com.sougata.form_data_service.model.FormResponse;
import com.sougata.form_data_service.model.LinearScale;
import com.sougata.form_data_service.repository.LinearScaleRepository;
import com.sougata.form_data_service.repository.QuestionResponseRepository;
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
        LinearScaleResponseQuestionDto.Summary
        > {

    private final LinearScaleRepository linearScaleRepository;

    @Autowired
    public LinearScaleManager(LinearScaleRepository linearScaleRepository, QuestionResponseRepository questionResponseRepository) {
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

            res.setScale(g.get("scale", Integer.class));
            res.setResponseCount(g.get("responseCount", Long.class));
            res.setResponseIds(Arrays.stream(g.get("responseIds", Long[].class)).map(Object::toString).toList());
            
            return res;
        }).toList();

        ls.setResponses(responses);

        return ls;
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
