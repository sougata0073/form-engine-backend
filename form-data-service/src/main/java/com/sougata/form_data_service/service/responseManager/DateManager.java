package com.sougata.form_data_service.service.responseManager;

import com.sougata.form_data_service.constant.QuestionType;
import com.sougata.form_data_service.dto.question.request.DateResponseAddReqDto;
import com.sougata.form_data_service.dto.question.response.DateResDto;
import com.sougata.form_data_service.dto.response.question.DateResponseQuestionDto;
import com.sougata.form_data_service.dto.response.summary.DateResponseSummaryDto;
import com.sougata.form_data_service.model.Date;
import com.sougata.form_data_service.model.FormResponse;
import com.sougata.form_data_service.repository.DateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service("DATE_RESPONSE_MANAGER")
public class DateManager extends ResponseManager<DateResponseAddReqDto, DateResponseSummaryDto, DateResDto, DateResponseQuestionDto> {

    private final DateRepository dateRepository;

    @Autowired
    public DateManager(DateRepository dateRepository) {
        this.dateRepository = dateRepository;
    }

    @Override
    public void create(DateResponseAddReqDto response, FormResponse formResponse) {
        Date date = new Date();
        date.setDate(response.getDate());
        date.setQuestionId(response.getQuestionId());
        date.setFormResponse(formResponse);

        dateRepository.save(date);
    }

    @Override
    public List<DateResponseSummaryDto> getResponseSummaries(UUID formId, List<DateResDto> questionResponses) {
        var responseSummaries = dateRepository.getResponseSummaries(formId);
        var result = new ArrayList<DateResponseSummaryDto>();

        var responseDateMap = dateRepository.getResponseDates(formId)
                .stream().collect(Collectors.groupingBy(e -> e.get("questionId", Long.class)));

        questionResponses.forEach(qr ->
                result.add(
                        responseSummaries.stream()
                                .filter(rs -> Objects.equals(rs.questionId(), qr.getId()))
                                .map(rs -> {
                                    var d = new DateResponseSummaryDto();

                                    d.setQuestionId(qr.getId());
                                    d.setQuestion(qr.getQuestion());
                                    d.setOrderIndex(qr.getOrderIndex());
                                    d.setNumberOfResponses(rs.numberOfResponses());
                                    d.setQuestionType(QuestionType.DATE);

                                    d.setResponses(
                                            responseDateMap.get(rs.questionId())
                                                    .stream().map(tuple -> tuple.get("date", Instant.class)).toList()
                                    );

                                    return d;
                                })
                                .findFirst()
                                .orElseGet(() -> {
                                    var d = new DateResponseSummaryDto();

                                    d.setQuestionId(qr.getId());
                                    d.setQuestion(qr.getQuestion());
                                    d.setOrderIndex(qr.getOrderIndex());
                                    d.setNumberOfResponses(0L);
                                    d.setQuestionType(QuestionType.DATE);
                                    d.setResponses(List.of());

                                    return d;
                                })
                ));

        return result;
    }

    @Override
    public DateResponseQuestionDto getResponseByQuestion(UUID formId, DateResDto questionRes) {
        var grouped = dateRepository.groupedByDate(formId, questionRes.getId());

        var totalResponseCount = grouped.stream()
                .mapToLong(g -> g.get("responseCount", Long.class).intValue())
                .sum();

        var d = new DateResponseQuestionDto();

        var responses = grouped.stream().map(g -> {
            Instant date = g.get("date", Instant.class);

            return new DateResponseQuestionDto.Response(
                    date,
                    g.get("responseCount", Long.class).intValue(),
                    Arrays.stream(g.get("responseIds", Long[].class)).map(Object::toString).toList()
            );

        }).toList();

        d.setQuestionId(questionRes.getId());
        d.setQuestion(questionRes.getQuestion());
        d.setQuestionType(questionRes.getQuestionType());
        d.setResponses(responses);
        d.setTotalResponseCount(totalResponseCount);

        return d;
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.DATE;
    }

    @Override
    public void deleteResponses(UUID formId, Long questionId) {
        var entities = dateRepository.findByFormIdAndQuestionId(formId, questionId);

        dateRepository.deleteAll(entities);
    }
}
