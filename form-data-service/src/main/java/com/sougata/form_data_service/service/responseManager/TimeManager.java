package com.sougata.form_data_service.service.responseManager;

import com.sougata.form_data_service.constant.QuestionType;
import com.sougata.form_data_service.dto.question.request.TimeResponseAddReqDto;
import com.sougata.form_data_service.dto.response.question.TimeResponseQuestionDto;
import com.sougata.form_data_service.dto.response.summary.TimeResponseSummaryDto;
import com.sougata.form_data_service.form_schema.dto.questionSchema.response.TimeResDto;
import com.sougata.form_data_service.model.FormResponse;
import com.sougata.form_data_service.model.Time;
import com.sougata.form_data_service.repository.TimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service("TIME_RESPONSE_MANAGER")
public class TimeManager extends ResponseManager<TimeResponseAddReqDto, TimeResponseSummaryDto, TimeResDto, TimeResponseQuestionDto> {

    private final TimeRepository timeRepository;

    @Autowired
    public TimeManager(TimeRepository timeRepository) {
        this.timeRepository = timeRepository;
    }

    @Override
    public void create(TimeResponseAddReqDto response, FormResponse formResponse) {
        Time time = new Time();
        time.setTime(response.getTime());
        time.setQuestionId(response.getQuestionId());
        time.setFormResponse(formResponse);

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
    public TimeResponseQuestionDto getResponseByQuestion(UUID formId, TimeResDto questionRes) {
        var grouped = timeRepository.groupedByTime(formId, questionRes.getId());

        var t = new TimeResponseQuestionDto();

        var responses = grouped.stream().map(g -> {
            Instant time = g.get("time", Instant.class);

            return new TimeResponseQuestionDto.Response(
                    time,
                    g.get("responseCount", Long.class).intValue(),
                    Arrays.stream(g.get("responseIds", Long[].class)).map(Object::toString).toList()
            );

        }).toList();

        t.setQuestionId(questionRes.getId());
        t.setQuestion(questionRes.getQuestion());
        t.setQuestionType(questionRes.getQuestionType());
        t.setResponses(responses);

        return t;
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.TIME;
    }

    @Override
    public void deleteResponses(UUID formId, Long questionId) {
        var entities = timeRepository.findByFormIdAndQuestionId(formId, questionId);

        timeRepository.deleteAll(entities);
    }
}
