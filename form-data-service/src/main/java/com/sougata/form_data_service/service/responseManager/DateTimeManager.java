package com.sougata.form_data_service.service.responseManager;

import com.sougata.form_data_service.constant.QuestionType;
import com.sougata.form_data_service.dto.question.request.DateTimeResponseAddReqDto;
import com.sougata.form_data_service.dto.question.response.DateTimeResDto;
import com.sougata.form_data_service.dto.response.question.DateTimeResponseQuestionDto;
import com.sougata.form_data_service.dto.response.summary.DateTimeResponseSummaryDto;
import com.sougata.form_data_service.model.DateTime;
import com.sougata.form_data_service.model.FormResponse;
import com.sougata.form_data_service.repository.DateTimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service("DATE_TIME_RESPONSE_MANAGER")
public class DateTimeManager extends ResponseManager<DateTimeResponseAddReqDto, DateTimeResponseSummaryDto, DateTimeResDto, DateTimeResponseQuestionDto> {

    private final DateTimeRepository dateTimeRepository;

    @Autowired
    public DateTimeManager(DateTimeRepository dateTimeRepository) {
        this.dateTimeRepository = dateTimeRepository;
    }

    @Override
    public void create(DateTimeResponseAddReqDto response, FormResponse formResponse) {
        DateTime dateTime = new DateTime();
        dateTime.setDateTime(response.getDateTime());
        dateTime.setQuestionId(response.getQuestionId());
        dateTime.setFormResponse(formResponse);

        dateTimeRepository.save(dateTime);
    }

    @Override
    public List<DateTimeResponseSummaryDto> getResponseSummaries(UUID formId, List<DateTimeResDto> questionResponses) {
        var responseSummaries = dateTimeRepository.getResponseSummaries(formId);
        var result = new ArrayList<DateTimeResponseSummaryDto>();

        var responseDateTimeMap = dateTimeRepository.getResponseDateTimes(formId)
                .stream().collect(Collectors.groupingBy(e -> e.get("questionId", Long.class)));

        questionResponses.forEach(qr ->
                result.add(
                        responseSummaries.stream()
                                .filter(rs -> Objects.equals(rs.questionId(), qr.getId()))
                                .map(rs -> {
                                    var dt = new DateTimeResponseSummaryDto();

                                    dt.setQuestionId(qr.getId());
                                    dt.setQuestion(qr.getQuestion());
                                    dt.setOrderIndex(qr.getOrderIndex());
                                    dt.setNumberOfResponses(rs.numberOfResponses());
                                    dt.setQuestionType(QuestionType.DATE_TIME);

                                    dt.setResponses(
                                            responseDateTimeMap.get(rs.questionId())
                                                    .stream().map(tuple -> tuple.get("dateTime", Instant.class)).toList()
                                    );

                                    return dt;
                                })
                                .findFirst()
                                .orElseGet(() -> {
                                    var dt = new DateTimeResponseSummaryDto();

                                    dt.setQuestionId(qr.getId());
                                    dt.setQuestion(qr.getQuestion());
                                    dt.setOrderIndex(qr.getOrderIndex());
                                    dt.setNumberOfResponses(0L);
                                    dt.setQuestionType(QuestionType.DATE_TIME);
                                    dt.setResponses(List.of());

                                    return dt;
                                })
                ));

        return result;
    }

    @Override
    public DateTimeResponseQuestionDto getResponseByQuestion(UUID formId, DateTimeResDto questionRes) {
        var grouped = dateTimeRepository.groupedByDateTimes(formId, questionRes.getId());

        var totalResponseCount = grouped.stream()
                .mapToLong(g -> g.get("responseCount", Long.class).intValue())
                .sum();

        var dt = new DateTimeResponseQuestionDto();

        var responses = grouped.stream().map(g -> {
            Instant dateTime = g.get("dateTime", Instant.class);

            return new DateTimeResponseQuestionDto.Response(
                    dateTime,
                    g.get("responseCount", Long.class).intValue(),
                    Arrays.stream(g.get("responseIds", Long[].class)).map(Object::toString).toList()
            );

        }).toList();

        dt.setQuestionId(questionRes.getId());
        dt.setQuestion(questionRes.getQuestion());
        dt.setQuestionType(questionRes.getQuestionType());
        dt.setResponses(responses);
        dt.setTotalResponseCount(totalResponseCount);

        return dt;
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.DATE_TIME;
    }

    @Override
    public void deleteResponses(UUID formId, Long questionId) {
        var entities = dateTimeRepository.findByFormIdAndQuestionId(formId, questionId);

        dateTimeRepository.deleteAll(entities);
    }
}
