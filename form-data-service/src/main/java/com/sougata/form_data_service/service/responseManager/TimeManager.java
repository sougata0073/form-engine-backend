package com.sougata.form_data_service.service.responseManager;

import com.sougata.form_data_service.constant.QuestionType;
import com.sougata.form_data_service.dto.question.request.TimeResponseAddReqDto;
import com.sougata.form_data_service.dto.question.response.TimeResDto;
import com.sougata.form_data_service.dto.response.individual.ParagraphResponseIndividualDto;
import com.sougata.form_data_service.dto.response.individual.ResponseIndividualDto;
import com.sougata.form_data_service.dto.response.individual.TimeResponseIndividualDto;
import com.sougata.form_data_service.dto.response.question.TimeResponseQuestionDto;
import com.sougata.form_data_service.dto.response.summary.TimeResponseSummaryDto;
import com.sougata.form_data_service.feignClient.AuthServiceFeignClient;
import com.sougata.form_data_service.model.FormResponse;
import com.sougata.form_data_service.model.Time;
import com.sougata.form_data_service.repository.FormResponseRepository;
import com.sougata.form_data_service.repository.QuestionResponseRepository;
import com.sougata.form_data_service.repository.TimeRepository;
import com.sougata.form_data_service.util.IdUtil;
import jakarta.persistence.Tuple;
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
        TimeResponseQuestionDto.Summary,
        TimeResponseIndividualDto
        > {

    private final TimeRepository timeRepository;

    @Autowired
    public TimeManager(TimeRepository timeRepository, QuestionResponseRepository questionResponseRepository, FormResponseRepository formResponseRepository, AuthServiceFeignClient authServiceFeignClient) {
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

        var responseMap = timeRepository.getResponsesTimes(formId)
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
                                    t.setQuestionType(getQuestionType());

                                    var responses = responseMap.get(rs.questionId()).stream()
                                            .map(tuple -> {
                                                var res = new TimeResponseSummaryDto.Response();

                                                var times = tuple.get("times", String[].class);
                                                var timeCounts = tuple.get("timeCounts", Long[].class);

                                                var timeCountPairs = new ArrayList<TimeResponseSummaryDto.TimeCountPair>();

                                                for (int i = 0; i < times.length; i++) {
                                                    timeCountPairs.add(
                                                            new TimeResponseSummaryDto.TimeCountPair(
                                                                    Instant.parse(times[i]), timeCounts[i]
                                                            )
                                                    );
                                                }

                                                res.setHour(tuple.get("hour", Integer.class));
                                                res.setTimes(timeCountPairs);

                                                return res;
                                            }).toList();

                                    t.setResponses(responses);

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
    public TimeResponseSummaryDto getResponseSummary(UUID formId, Long questionId, TimeResDto questionRes, Pageable pageable) {
        return null;
        /*
        var responseSummary = timeRepository.getResponseSummary(formId, questionId);
        var timeResponses = timeRepository.getResponseTimes(formId, questionId, pageable);

        var t = new TimeResponseSummaryDto();

        t.setQuestionId(questionRes.getId());
        t.setQuestion(questionRes.getQuestion());
        t.setOrderIndex(questionRes.getOrderIndex());
        t.setNumberOfResponses(responseSummary.numberOfResponses());
        t.setQuestionType(getQuestionType());

        var responses = timeResponses.stream()
                .map(tuple -> {
                    var res = new TimeResponseSummaryDto.Response();

                    var times = Arrays.asList(tuple.get("times", String[].class));
                    var timeCounts = Arrays.asList(tuple.get("timeCounts", Long[].class));

                    var timeCountPairs = new ArrayList<TimeResponseSummaryDto.TimeCountPair>();

                    for (int i = 0; i < times.size(); i++) {
                        timeCountPairs.add(
                                new TimeResponseSummaryDto.TimeCountPair(Instant.parse(times.get(i)), timeCounts.get(i))
                        );
                    }

                    res.setHour(tuple.get("hour", Integer.class));
                    res.setTimes(timeCountPairs);

                    return res;
                }).toList();

        t.setResponses(responses);

        return t;
         */
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

            res.setQuestionId(questionId);
            res.setQuestionType(getQuestionType());
            res.setTime(g.get("time", Instant.class));
            res.setResponseCount(g.get("responseCount", Long.class));

            var map = new HashMap<String, List<String>>();

            map.put("time", List.of(res.getTime() == null ? "" : res.getTime().toString()));

            res.setFormResponsesIdentifier(IdUtil.generateCompressedEncodedId(map));

            return res;

        }).toList();

        t.setQuestionId(questionId);
        t.setQuestionType(getQuestionType());
        t.setResponses(responses);

        return t;
    }

    @Override
    public List<TimeResponseIndividualDto> getIndividualResponses(UUID formId, Long formResponseId) {
        var responses = timeRepository.getTimesByFormResponse(formId, formResponseId);

        return responses.stream().map(tuple -> {
            var qId = tuple.get("questionId", Long.class);
            var time = tuple.get("time", Instant.class);

            var res = new TimeResponseIndividualDto();

            res.setQuestionId(qId);
            res.setQuestionType(getQuestionType());
            res.setTime(time);

            return res;
        }).toList();
    }

    @Override
    public List<Tuple> getFormResponseAndUserIds(UUID formId, Long questionId, String formResponsesIdentifier, Pageable pageable) {
        return List.of();
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
