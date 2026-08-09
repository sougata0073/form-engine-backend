package com.sougata.form_data_service.service.responseManager;

import com.sougata.form_data_service.constant.QuestionType;
import com.sougata.form_data_service.dto.question.request.DurationResponseAddReqDto;
import com.sougata.form_data_service.dto.question.response.DurationResDto;
import com.sougata.form_data_service.dto.response.individual.DropdownResponseIndividualDto;
import com.sougata.form_data_service.dto.response.individual.DurationResponseIndividualDto;
import com.sougata.form_data_service.dto.response.individual.ResponseIndividualDto;
import com.sougata.form_data_service.dto.response.question.DurationResponseQuestionDto;
import com.sougata.form_data_service.dto.response.summary.DurationResponseSummaryDto;
import com.sougata.form_data_service.feignClient.AuthServiceFeignClient;
import com.sougata.form_data_service.model.Duration;
import com.sougata.form_data_service.model.FormResponse;
import com.sougata.form_data_service.repository.DurationRepository;
import com.sougata.form_data_service.repository.FormResponseRepository;
import com.sougata.form_data_service.repository.QuestionResponseRepository;
import com.sougata.form_data_service.util.IdUtil;
import jakarta.persistence.Tuple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service("DURATION_RESPONSE_MANAGER")
public class DurationManager extends ResponseManager<
        DurationResponseAddReqDto,
        DurationResponseSummaryDto,
        DurationResDto,
        DurationResponseQuestionDto,
        DurationResponseQuestionDto.Response,
        DurationResponseQuestionDto.Summary,
        DurationResponseIndividualDto
        > {

    private final DurationRepository durationRepository;

    @Autowired
    public DurationManager(DurationRepository durationRepository, QuestionResponseRepository questionResponseRepository, FormResponseRepository formResponseRepository, AuthServiceFeignClient authServiceFeignClient) {
        super(questionResponseRepository);
        this.durationRepository = durationRepository;
    }

    @Override
    @Transactional
    public void create(DurationResponseAddReqDto response, FormResponse formResponse) {
        Duration duration = new Duration();

        var qr = createQuestionResponse(response.getQuestionId(), formResponse);

        duration.setHours(response.getHours());
        duration.setMinutes(response.getMinutes());
        duration.setSeconds(response.getSeconds());
        duration.setQuestionResponse(qr);

        durationRepository.save(duration);
    }

    @Override
    public List<DurationResponseSummaryDto> getResponseSummaries(UUID formId, List<DurationResDto> questionResponses) {
        var responseSummaries = durationRepository.getResponseSummaries(formId);
        var result = new ArrayList<DurationResponseSummaryDto>();

        questionResponses.forEach(qr ->
                result.add(
                        responseSummaries.stream()
                                .filter(rs -> Objects.equals(rs.questionId(), qr.getId()))
                                .map(rs -> {
                                    var d = new DurationResponseSummaryDto();

                                    d.setQuestionId(qr.getId());
                                    d.setQuestion(qr.getQuestion());
                                    d.setOrderIndex(qr.getOrderIndex());
                                    d.setNumberOfResponses(rs.numberOfResponses());
                                    d.setQuestionType(getQuestionType());

                                    var durations = durationRepository.getResponseDurations(formId, rs.questionId(), Pageable.ofSize(20));

                                    var responses = durations.stream().map(tuple -> {
                                                var res = new DurationResponseSummaryDto.Response();

                                                res.setHours(tuple.get("hours", Integer.class));

                                                var minutes = tuple.get("minutes", Integer[].class);
                                                var seconds = tuple.get("seconds", Integer[].class);
                                                var minSecCounts = tuple.get("minSecCounts", Long[].class);

                                                var durationCountPairs = new ArrayList<DurationResponseSummaryDto.DurationCountPair>();

                                                for (int i = 0; i < minutes.length; i++) {
                                                    var min = minutes[i];
                                                    var sec = seconds[i];
                                                    var cnt = minSecCounts[i];

                                                    durationCountPairs.add(
                                                            new DurationResponseSummaryDto.DurationCountPair(
                                                                    min, sec, cnt
                                                            )
                                                    );
                                                }

                                                res.setDurations(durationCountPairs);

                                                return res;
                                            }).toList();

                                    d.setResponses(responses);

                                    return d;
                                })
                                .findFirst()
                                .orElseGet(() -> {
                                    var d = new DurationResponseSummaryDto();

                                    d.setQuestionId(qr.getId());
                                    d.setQuestion(qr.getQuestion());
                                    d.setOrderIndex(qr.getOrderIndex());
                                    d.setNumberOfResponses(0L);
                                    d.setQuestionType(QuestionType.DURATION);
                                    d.setResponses(List.of());

                                    return d;
                                })
                ));

        return result;
    }

    @Override
    public DurationResponseSummaryDto getResponseSummary(UUID formId, Long questionId, DurationResDto questionRes, Pageable pageable) {
        var responseSummary = durationRepository.getResponseSummary(formId, questionId);
        var durations = durationRepository.getResponseDurations(formId, questionId, pageable);

        var d = new DurationResponseSummaryDto();

        d.setQuestionId(questionRes.getId());
        d.setQuestion(questionRes.getQuestion());
        d.setOrderIndex(questionRes.getOrderIndex());
        d.setNumberOfResponses(responseSummary.numberOfResponses());
        d.setQuestionType(getQuestionType());

        var responses = durations.stream().map(tuple -> {
            var res = new DurationResponseSummaryDto.Response();

            res.setHours(tuple.get("hours", Integer.class));

            var minutes = tuple.get("minutes", Integer[].class);
            var seconds = tuple.get("seconds", Integer[].class);
            var minSecCounts = tuple.get("minSecCounts", Long[].class);

            var durationCountPairs = new ArrayList<DurationResponseSummaryDto.DurationCountPair>();

            for (int i = 0; i < minutes.length; i++) {
                var min = minutes[i];
                var sec = seconds[i];
                var cnt = minSecCounts[i];

                durationCountPairs.add(
                        new DurationResponseSummaryDto.DurationCountPair(
                                min, sec, cnt
                        )
                );
            }

            res.setDurations(durationCountPairs);

            return res;
        }).toList();

        d.setResponses(responses);

        return d;
    }

    @Override
    public DurationResponseQuestionDto.Summary getResponseByQuestionSummary(UUID formId, DurationResDto questionResponse) {
        return new DurationResponseQuestionDto.Summary();
    }

    @Override
    public DurationResponseQuestionDto getResponseByQuestion(UUID formId, Long questionId, Map<String, String> extraParams, Pageable pageable) {
        var grouped = durationRepository.groupedByDuration(formId, questionId, pageable);

        var d = new DurationResponseQuestionDto();

        var responses = grouped.stream().map(g -> {
            Integer hours = g.get("hours", Integer.class);
            Integer minutes = g.get("minutes", Integer.class);
            Integer seconds = g.get("seconds", Integer.class);

            var res = new DurationResponseQuestionDto.Response();

            res.setQuestionId(questionId);
            res.setQuestionType(getQuestionType());
            res.setHours(hours);
            res.setMinutes(minutes);
            res.setSeconds(seconds);
            res.setResponseCount(g.get("responseCount", Long.class));

            var map = new HashMap<String, List<String>>();

            map.put("hours", List.of(res.getHours() == null ? "" : res.getHours().toString()));
            map.put("minutes", List.of(res.getMinutes() == null ? "" : res.getMinutes().toString()));
            map.put("seconds", List.of(res.getSeconds() == null ? "" : res.getSeconds().toString()));

            res.setFormResponsesIdentifier(IdUtil.generateCompressedEncodedId(map));

            return res;

        }).toList();

        d.setQuestionId(questionId);
        d.setQuestionType(getQuestionType());
        d.setResponses(responses);

        return d;
    }

    @Override
    public List<DurationResponseIndividualDto> getIndividualResponses(UUID formId, Long formResponseId) {
        var responses = durationRepository.getDurationsByFormResponse(formId, formResponseId);

        return responses.stream().map(tuple -> {
            var qId = tuple.get("questionId", Long.class);
            var hours = tuple.get("hours", Integer.class);
            var minutes = tuple.get("minutes", Integer.class);
            var seconds = tuple.get("seconds", Integer.class);

            var res = new DurationResponseIndividualDto();

            res.setQuestionId(qId);
            res.setQuestionType(getQuestionType());
            res.setHours(hours);
            res.setMinutes(minutes);
            res.setSeconds(seconds);

            return res;
        }).toList();
    }

    @Override
    public List<Tuple> getFormResponseAndUserIds(UUID formId, Long questionId, String formResponsesIdentifier, Pageable pageable) {
        var map = IdUtil.reconstructCompressedEncodedId(formResponsesIdentifier);

        var hours = map.get("hours");
        var minutes = map.get("minutes");
        var seconds = map.get("seconds");

        if (hours.isEmpty() || minutes.isEmpty() || seconds.isEmpty()) {
            throw new IllegalArgumentException("Invalid Form Responses Identifier. Identifier: " + formResponsesIdentifier);
        }

        var h = Integer.parseInt(hours.getFirst());
        var m = Integer.parseInt(minutes.getFirst());
        var s = Integer.parseInt(seconds.getFirst());

        return durationRepository.getResponseIdsByGroupedResponse(formId, questionId, h, m, s, pageable);
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.DURATION;
    }

    @Override
    public void deleteResponses(UUID formId, Long questionId) {
        durationRepository.deleteAllByFormIdAndQuestionId(formId, questionId);
    }
}
