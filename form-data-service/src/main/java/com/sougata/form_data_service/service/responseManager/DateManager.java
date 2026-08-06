package com.sougata.form_data_service.service.responseManager;

import com.sougata.form_data_service.constant.QuestionType;
import com.sougata.form_data_service.dto.question.request.DateResponseAddReqDto;
import com.sougata.form_data_service.dto.question.response.DateResDto;
import com.sougata.form_data_service.dto.response.individual.CheckboxResponseIndividualDto;
import com.sougata.form_data_service.dto.response.individual.DateResponseIndividualDto;
import com.sougata.form_data_service.dto.response.question.DateResponseQuestionDto;
import com.sougata.form_data_service.dto.response.summary.DateResponseSummaryDto;
import com.sougata.form_data_service.model.Date;
import com.sougata.form_data_service.model.FormResponse;
import com.sougata.form_data_service.repository.DateRepository;
import com.sougata.form_data_service.repository.QuestionResponseRepository;
import com.sougata.form_data_service.util.IdUtil;
import jakarta.persistence.Tuple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service("DATE_RESPONSE_MANAGER")
public class DateManager extends ResponseManager<
        DateResponseAddReqDto,
        DateResponseSummaryDto,
        DateResDto,
        DateResponseQuestionDto,
        DateResponseQuestionDto.Response,
        DateResponseQuestionDto.Summary,
        DateResponseIndividualDto
        > {

    private final DateRepository dateRepository;
    private final QuestionResponseRepository questionResponseRepository;

    @Autowired
    public DateManager(DateRepository dateRepository, QuestionResponseRepository questionResponseRepository) {
        super(questionResponseRepository);
        this.dateRepository = dateRepository;
        this.questionResponseRepository = questionResponseRepository;
    }

    @Override
    @Transactional
    public void create(DateResponseAddReqDto response, FormResponse formResponse) {
        Date date = new Date();
        var qr = createQuestionResponse(response.getQuestionId(), formResponse);

        date.setQuestionResponse(qr);
        date.setDate(response.getDate());

        dateRepository.save(date);
    }

    @Override
    public List<DateResponseSummaryDto> getResponseSummaries(UUID formId, List<DateResDto> questionResponses) {
        var responseSummaries = dateRepository.getResponseSummaries(formId);
        var result = new ArrayList<DateResponseSummaryDto>();

        var responseMap = dateRepository.getResponsesDates(formId, Pageable.ofSize(20))
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

                                    var responses = responseMap.get(rs.questionId()).stream().map(tuple -> {
                                        var res = new DateResponseSummaryDto.Response();

                                        res.setYear(tuple.get("year", Integer.class));
                                        res.setMonth(tuple.get("month", Integer.class));

                                        var dates = tuple.get("dates", String[].class);
                                        var dateCounts = tuple.get("dateCounts", Long[].class);

                                        var dateCountPairs = new ArrayList<DateResponseSummaryDto.DateCountPair>();

                                        for (int i = 0; i < dates.length; i++) {
                                            dateCountPairs.add(
                                                    new DateResponseSummaryDto.DateCountPair(
                                                            Instant.parse(dates[i]), dateCounts[i]
                                                    )
                                            );
                                        }

                                        res.setDates(dateCountPairs);

                                        return res;
                                    }).toList();

                                    d.setResponses(responses);

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
    public DateResponseSummaryDto getResponseSummary(UUID formId, Long questionId, DateResDto questionRes, Pageable pageable) {
        var responseSummary = dateRepository.getResponseSummary(formId, questionId);
        var dateTimes = dateRepository.getResponseDates(formId, questionId, pageable);

        var d = new DateResponseSummaryDto();

        d.setQuestionId(questionRes.getId());
        d.setQuestion(questionRes.getQuestion());
        d.setOrderIndex(questionRes.getOrderIndex());
        d.setNumberOfResponses(responseSummary.numberOfResponses());
        d.setQuestionType(getQuestionType());

        var responses = dateTimes.stream().map(tuple -> {
            var res = new DateResponseSummaryDto.Response();

            res.setYear(tuple.get("year", Integer.class));
            res.setMonth(tuple.get("month", Integer.class));

            var dates = tuple.get("dates", String[].class);
            var dateCounts = tuple.get("dateCounts", Long[].class);

            var dateCountPairs = new ArrayList<DateResponseSummaryDto.DateCountPair>();

            for (int i = 0; i < dates.length; i++) {
                dateCountPairs.add(
                        new DateResponseSummaryDto.DateCountPair(
                                Instant.parse(dates[i]), dateCounts[i]
                        )
                );
            }

            res.setDates(dateCountPairs);

            return res;
        }).toList();

        d.setResponses(responses);

        return d;
    }

    @Override
    public DateResponseQuestionDto.Summary getResponseByQuestionSummary(UUID formId, DateResDto questionResponse) {
        var sum = new DateResponseQuestionDto.Summary();

        sum.setQuestionId(questionResponse.getId());
        sum.setQuestion(questionResponse.getQuestion());
        sum.setQuestionType(questionResponse.getQuestionType());
        sum.setTotalResponseCount(getTotalResponseCount(formId, questionResponse.getId()));
        sum.setDistinctResponseCount(dateRepository.getDistinctResponseCount(formId, questionResponse.getId()));

        return sum;
    }

    @Override
    public DateResponseQuestionDto getResponseByQuestion(UUID formId, Long questionId, Map<String, String> extraParams, Pageable pageable) {
        var grouped = dateRepository.groupedByDate(formId, questionId, pageable);

        var d = new DateResponseQuestionDto();

        var responses = grouped.stream().map(g -> {
            var res = new DateResponseQuestionDto.Response();

            res.setQuestionId(questionId);
            res.setQuestionType(getQuestionType());
            res.setDate(g.get("date", Instant.class));
            res.setResponseCount(g.get("responseCount", Long.class));

            var map = new HashMap<String, List<String>>();

            map.put("date", List.of(res.getDate() == null ? "" : res.getDate().toString()));

            res.setFormResponsesIdentifier(IdUtil.generateCompressedEncodedId(map));

            return res;

        }).toList();

        d.setQuestionId(questionId);
        d.setQuestionType(getQuestionType());
        d.setResponses(responses);

        return d;
    }

    @Override
    public List<DateResponseIndividualDto> getIndividualResponses(UUID formId, Long formResponseId) {
        var responses = dateRepository.getDatesByFormResponse(formId, formResponseId);

        return responses.stream().map(tuple -> {
            var qId = tuple.get("questionId", Long.class);
            var date = tuple.get("date", Instant.class);

            var res = new DateResponseIndividualDto();

            res.setQuestionId(qId);
            res.setQuestionType(getQuestionType());
            res.setDate(date);

            return res;
        }).toList();
    }

    @Override
    public List<Tuple> getFormResponseAndUserIds(UUID formId, Long questionId, String formResponsesIdentifier, Pageable pageable) {
        return List.of();
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.DATE;
    }

    @Override
    public void deleteResponses(UUID formId, Long questionId) {
        dateRepository.deleteAllByFormIdAndQuestionId(formId, questionId);
    }
}
