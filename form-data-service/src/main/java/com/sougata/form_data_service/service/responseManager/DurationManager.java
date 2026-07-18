package com.sougata.form_data_service.service.responseManager;

import com.sougata.form_data_service.constant.QuestionType;
import com.sougata.form_data_service.dto.question.request.DurationResponseAddReqDto;
import com.sougata.form_data_service.dto.question.response.DurationResDto;
import com.sougata.form_data_service.dto.response.question.DateResponseQuestionDto;
import com.sougata.form_data_service.dto.response.question.DurationResponseQuestionDto;
import com.sougata.form_data_service.dto.response.summary.DurationResponseSummaryDto;
import com.sougata.form_data_service.model.Duration;
import com.sougata.form_data_service.model.FormResponse;
import com.sougata.form_data_service.repository.DurationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service("DURATION_RESPONSE_MANAGER")
public class DurationManager extends ResponseManager<DurationResponseAddReqDto, DurationResponseSummaryDto, DurationResDto, DurationResponseQuestionDto> {

    private final DurationRepository durationRepository;

    @Autowired
    public DurationManager(DurationRepository durationRepository) {
        this.durationRepository = durationRepository;
    }

    @Override
    public void create(DurationResponseAddReqDto response, FormResponse formResponse) {
        Duration duration = new Duration();
        duration.setHours(response.getHours());
        duration.setMinutes(response.getMinutes());
        duration.setSeconds(response.getSeconds());
        duration.setQuestionId(response.getQuestionId());
        duration.setFormResponse(formResponse);

        durationRepository.save(duration);
    }

    @Override
    public List<DurationResponseSummaryDto> getResponseSummaries(UUID formId, List<DurationResDto> questionResponses) {
        var responseSummaries = durationRepository.getResponseSummaries(formId);
        var result = new ArrayList<DurationResponseSummaryDto>();

        var responseDurationMap = durationRepository.getResponseDurations(formId)
                .stream().collect(Collectors.groupingBy(e -> e.get("questionId", Long.class)));

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
                                    d.setQuestionType(QuestionType.DURATION);
                                    d.setResponses(
                                            responseDurationMap.get(rs.questionId())
                                                    .stream()
                                                    .map(tuple -> new DurationResponseSummaryDto.Response(
                                                            tuple.get("hours", Integer.class),
                                                            tuple.get("minutes", Integer.class),
                                                            tuple.get("seconds", Integer.class)
                                                    ))
                                                    .toList()
                                    );

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
    public DurationResponseQuestionDto getResponseByQuestion(UUID formId, DurationResDto questionRes) {
        var grouped = durationRepository.groupedByDuration(formId, questionRes.getId());

        var d = new DurationResponseQuestionDto();

        var responses = grouped.stream().map(g -> {
            Integer hours = g.get("hours", Integer.class);
            Integer minutes = g.get("minutes", Integer.class);
            Integer seconds = g.get("seconds", Integer.class);

            return new DurationResponseQuestionDto.Response(
                    hours,
                    minutes,
                    seconds,
                    g.get("responseCount", Long.class).intValue(),
                    Arrays.stream(g.get("responseIds", Long[].class)).map(Object::toString).toList()
            );

        }).toList();

        d.setQuestionId(questionRes.getId());
        d.setQuestion(questionRes.getQuestion());
        d.setQuestionType(questionRes.getQuestionType());
        d.setResponses(responses);

        return d;
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.DURATION;
    }

    @Override
    public void deleteResponses(UUID formId, Long questionId) {
        var entities = durationRepository.findByFormIdAndQuestionId(formId, questionId);

        durationRepository.deleteAll(entities);
    }
}
