package com.sougata.form_data_service.service.responseManager;

import com.sougata.form_data_service.constant.QuestionType;
import com.sougata.form_data_service.dto.question.request.TimeResponseAddReqDto;
import com.sougata.form_data_service.dto.question.response.TimeResDto;
import com.sougata.form_data_service.dto.response.question.TimeResponseQuestionDto;
import com.sougata.form_data_service.dto.response.summary.TimeResponseSummaryDto;
import com.sougata.form_data_service.model.FormResponse;
import com.sougata.form_data_service.model.Time;
import com.sougata.form_data_service.repository.QuestionResponseRepository;
import com.sougata.form_data_service.repository.TimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service("TIME_RESPONSE_MANAGER")
public class TimeManager extends ResponseManager<
        TimeResponseAddReqDto,
        TimeResponseSummaryDto,
        TimeResDto,
        TimeResponseQuestionDto,
        TimeResponseQuestionDto.Response,
        TimeResponseQuestionDto.Summary
        > {

    private final TimeRepository timeRepository;

    @Autowired
    public TimeManager(TimeRepository timeRepository, QuestionResponseRepository questionResponseRepository) {
        super(questionResponseRepository);
        this.timeRepository = timeRepository;
    }

    @Override
    @Transactional
    public void create(TimeResponseAddReqDto response, FormResponse formResponse) {
        Time time = new Time();

        var qr = createQuestionResponse(response.getQuestionId(), formResponse);

        time.setTime(response.getTime());
        time.setQuestionResponse(qr);

        timeRepository.save(time);
    }

    @Override
    public List<TimeResponseSummaryDto> getResponseSummaries(UUID formId, List<TimeResDto> questionResponses) {
        var responseSummaries = timeRepository.getResponseSummaries(formId);
        var result = new ArrayList<TimeResponseSummaryDto>();

        var responseTimeMap = timeRepository.getResponseTimes(formId)
                .stream().collect(Collectors.groupingBy(e -> e.get("questionId", Long.class)));

        questionResponses.forEach(qr ->
                result.add(
                        responseSummaries.stream()
                                .filter(rs -> Objects.equals(rs.questionId(), qr.getId()))
                                .map(rs -> {
                                    var t = new TimeResponseSummaryDto();

                                    t.setQuestionId(qr.getId());
                                    t.setQuestion(qr.getQuestion());
                                    t.setOrderIndex(qr.getOrderIndex());
                                    t.setNumberOfResponses(rs.numberOfResponses());
                                    t.setQuestionType(QuestionType.TIME);

                                    t.setResponses(
                                            responseTimeMap.get(rs.questionId())
                                                    .stream().map(tuple -> tuple.get("time", Instant.class)).toList()
                                    );

                                    return t;
                                })
                                .findFirst()
                                .orElseGet(() -> {
                                    var t = new TimeResponseSummaryDto();

                                    t.setQuestionId(qr.getId());
                                    t.setQuestion(qr.getQuestion());
                                    t.setOrderIndex(qr.getOrderIndex());
                                    t.setNumberOfResponses(0L);
                                    t.setQuestionType(QuestionType.TIME);
                                    t.setResponses(List.of());

                                    return t;
                                })
                ));

        return result;
    }

    @Override
    public TimeResponseQuestionDto.Summary getResponseByQuestionSummary(UUID formId, TimeResDto questionResponse) {
        var sum = new TimeResponseQuestionDto.Summary();

        sum.setQuestionId(questionResponse.getId());
        sum.setQuestion(questionResponse.getQuestion());
        sum.setQuestionType(questionResponse.getQuestionType());
        sum.setTotalResponseCount(getTotalResponseCount(formId, questionResponse.getId()));
        sum.setDistinctResponseCount(timeRepository.getDistinctResponseCount(formId, questionResponse.getId()));

        return sum;
    }

    @Override
    public TimeResponseQuestionDto getResponseByQuestion(UUID formId, Long questionId, Map<String, String> extraParams, Pageable pageable) {
        var grouped = timeRepository.groupedByTime(formId, questionId, pageable);

        var t = new TimeResponseQuestionDto();

        var responses = grouped.stream().map(g -> {
            var res = new TimeResponseQuestionDto.Response();

            res.setTime(g.get("time", Instant.class));
            res.setResponseCount(g.get("responseCount", Long.class));
            res.setResponseIds(Arrays.stream(g.get("responseIds", Long[].class)).map(Object::toString).toList());

            return res;

        }).toList();

        t.setResponses(responses);

        return t;
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.TIME;
    }

    @Override
    public void deleteResponses(UUID formId, Long questionId) {
        timeRepository.deleteAllByFormIdAndQuestionId(formId, questionId);
    }
}
