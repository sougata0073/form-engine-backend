package com.sougata.form_data_service.service.responseManager;

import com.sougata.form_data_service.constant.QuestionType;
import com.sougata.form_data_service.dto.question.request.DateTimeResponseAddReqDto;
import com.sougata.form_data_service.dto.question.response.DateTimeResDto;
import com.sougata.form_data_service.dto.response.question.DateTimeResponseQuestionDto;
import com.sougata.form_data_service.dto.response.summary.DateTimeResponseSummaryDto;
import com.sougata.form_data_service.model.DateTime;
import com.sougata.form_data_service.model.FormResponse;
import com.sougata.form_data_service.repository.DateTimeRepository;
import com.sougata.form_data_service.repository.QuestionResponseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service("DATE_TIME_RESPONSE_MANAGER")
public class DateTimeManager extends ResponseManager<
        DateTimeResponseAddReqDto,
        DateTimeResponseSummaryDto,
        DateTimeResDto,
        DateTimeResponseQuestionDto,
        DateTimeResponseQuestionDto.Response,
        DateTimeResponseQuestionDto.Summary
        > {

    private final DateTimeRepository dateTimeRepository;

    @Autowired
    public DateTimeManager(DateTimeRepository dateTimeRepository, QuestionResponseRepository questionResponseRepository) {
        super(questionResponseRepository);
        this.dateTimeRepository = dateTimeRepository;
    }

    @Override
    @Transactional
    public void create(DateTimeResponseAddReqDto response, FormResponse formResponse) {
        DateTime dateTime = new DateTime();

        var qr = createQuestionResponse(response.getQuestionId(), formResponse);

        dateTime.setDateTime(response.getDateTime());
        dateTime.setQuestionResponse(qr);

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
    public DateTimeResponseQuestionDto.Summary getResponseByQuestionSummary(UUID formId, DateTimeResDto questionResponse) {
        var sum = new DateTimeResponseQuestionDto.Summary();

        sum.setQuestionId(questionResponse.getId());
        sum.setQuestion(questionResponse.getQuestion());
        sum.setQuestionType(questionResponse.getQuestionType());
        sum.setTotalResponseCount(getTotalResponseCount(formId, questionResponse.getId()));
        sum.setDistinctResponseCount(dateTimeRepository.getDistinctResponseCount(formId, questionResponse.getId()));

        return sum;
    }

    @Override
    public DateTimeResponseQuestionDto getResponseByQuestion(UUID formId, Long questionId, Map<String, String> extraParams, Pageable pageable) {
        var grouped = dateTimeRepository.groupedByDateTimes(formId, questionId, pageable);

        var dt = new DateTimeResponseQuestionDto();

        var responses = grouped.stream().map(g -> {
            var res = new DateTimeResponseQuestionDto.Response();

            res.setDateTime(g.get("dateTime", Instant.class));
            res.setResponseCount(g.get("responseCount", Long.class));
            res.setResponseIds(Arrays.stream(g.get("responseIds", Long[].class)).map(Object::toString).toList());

            return res;

        }).toList();

        dt.setResponses(responses);

        return dt;
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.DATE_TIME;
    }

    @Override
    public void deleteResponses(UUID formId, Long questionId) {
        dateTimeRepository.deleteAllByFormIdAndQuestionId(formId, questionId);
    }
}
