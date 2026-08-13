package com.sougata.form_data_service.service.responseManager;

import com.sougata.form_data_service.constant.QuestionType;
import com.sougata.form_data_service.dto.question.request.DateTimeResponseAddReqDto;
import com.sougata.form_data_service.dto.question.response.DateTimeResDto;
import com.sougata.form_data_service.dto.response.individual.DateResponseIndividualDto;
import com.sougata.form_data_service.dto.response.individual.DateTimeResponseIndividualDto;
import com.sougata.form_data_service.dto.response.question.DateTimeResponseQuestionDto;
import com.sougata.form_data_service.dto.response.summary.DateTimeResponseSummaryDto;
import com.sougata.form_data_service.model.DateTime;
import com.sougata.form_data_service.model.FormResponse;
import com.sougata.form_data_service.repository.DateTimeRepository;
import com.sougata.form_data_service.repository.QuestionResponseRepository;
import com.sougata.form_data_service.util.IdUtil;
import jakarta.persistence.Tuple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service("DATE_TIME_RESPONSE_MANAGER")
public class DateTimeManager extends ResponseManager<
        DateTimeResponseAddReqDto,
        DateTimeResponseSummaryDto,
        DateTimeResDto,
        DateTimeResponseQuestionDto,
        DateTimeResponseQuestionDto.Response,
        DateTimeResponseQuestionDto.Summary,
        DateTimeResponseIndividualDto
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
                                    dt.setQuestionType(getQuestionType());

//                                    var dateTimes = dateTimeRepository.getResponseDateTimes(formId, rs.questionId(), Pageable.ofSize(20));
//
//                                    var responses = dateTimes.stream().map(tuple -> {
//                                        var res = new DateTimeResponseSummaryDto.Response();
//
//                                        res.setDate(tuple.get("date", LocalDate.class));
//                                        res.setTime(tuple.get("time", Instant.class));
//                                        res.setTimeCount(tuple.get("timeCount", Long.class));
//
//                                        return res;
//                                    }).toList();
//
//                                    dt.setResponses(responses);
                                    dt.setResponses(List.of());

                                    return dt;
                                })
                                .findFirst()
                                .orElseGet(() -> {
                                    var dt = new DateTimeResponseSummaryDto();

                                    dt.setQuestionId(qr.getId());
                                    dt.setQuestion(qr.getQuestion());
                                    dt.setOrderIndex(qr.getOrderIndex());
                                    dt.setNumberOfResponses(0L);
                                    dt.setQuestionType(getQuestionType());
                                    dt.setResponses(List.of());

                                    return dt;
                                })
                ));

        return result;
    }

    @Override
    public DateTimeResponseSummaryDto getResponseSummary(UUID formId, Long questionId, DateTimeResDto questionRes, Pageable pageable) {
        var responseSummary = dateTimeRepository.getResponseSummary(formId, questionId);
        var dateTimes = dateTimeRepository.getResponseDateTimes(formId, questionId, pageable);

        var dt = new DateTimeResponseSummaryDto();

        dt.setQuestionId(questionRes.getId());
        dt.setQuestion(questionRes.getQuestion());
        dt.setOrderIndex(questionRes.getOrderIndex());
        dt.setNumberOfResponses(responseSummary.numberOfResponses());
        dt.setQuestionType(getQuestionType());

        var responses = dateTimes.stream().map(tuple -> {
            var res = new DateTimeResponseSummaryDto.Response();

            res.setDate(tuple.get("date", LocalDate.class));
            res.setTime(tuple.get("time", Instant.class));
            res.setTimeCount(tuple.get("timeCount", Long.class));

            return res;
        }).toList();

        dt.setResponses(responses);

        return dt;
    }

    @Override
    public DateTimeResponseQuestionDto.Summary getResponseByQuestionSummary(UUID formId, DateTimeResDto questionResponse) {
        return new DateTimeResponseQuestionDto.Summary();
    }

    @Override
    public DateTimeResponseQuestionDto getResponseByQuestion(UUID formId, Long questionId, Map<String, String> extraParams, Pageable pageable) {
        var grouped = dateTimeRepository.groupedByDateTimes(formId, questionId, pageable);

        var dt = new DateTimeResponseQuestionDto();

        var responses = grouped.stream().map(g -> {
            var res = new DateTimeResponseQuestionDto.Response();

            res.setQuestionId(questionId);
            res.setQuestionType(getQuestionType());
            res.setDateTime(g.get("dateTime", Instant.class));
            res.setResponseCount(g.get("responseCount", Long.class));

            var map = new HashMap<String, List<String>>();

            map.put("dateTime", List.of(res.getDateTime() == null ? "" : res.getDateTime().toString()));

            res.setFormResponsesIdentifier(IdUtil.generateCompressedEncodedId(map));

            return res;

        }).toList();

        dt.setQuestionId(questionId);
        dt.setQuestionType(getQuestionType());
        dt.setResponses(responses);

        return dt;
    }

    @Override
    public List<DateTimeResponseIndividualDto> getIndividualResponses(UUID formId, Long formResponseId) {
        var responses = dateTimeRepository.getDateTimesByFormResponse(formId, formResponseId);

        return responses.stream().map(tuple -> {
            var qId = tuple.get("questionId", Long.class);
            var dateTime = tuple.get("dateTime", Instant.class);

            var res = new DateTimeResponseIndividualDto();

            res.setQuestionId(qId);
            res.setQuestionType(getQuestionType());
            res.setDateTime(dateTime);

            return res;
        }).toList();
    }

    @Override
    public List<Tuple> getFormResponseAndUserIds(UUID formId, Long questionId, String formResponsesIdentifier, Pageable pageable) {
        var map = IdUtil.reconstructCompressedEncodedId(formResponsesIdentifier);

        var dateTime = map.get("dateTime");

        if (dateTime.isEmpty()) {
            throw new IllegalArgumentException("Invalid Form Responses Identifier. Identifier: " + formResponsesIdentifier);
        }

        var groupedResponse = Instant.parse(dateTime.getFirst());

        return dateTimeRepository.getResponseIdsByGroupedResponse(formId, questionId, groupedResponse, pageable);
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.DATE_TIME;
    }

    @Override
    public void deleteResponsesByQuestion(UUID formId, Long questionId) {
        dateTimeRepository.deleteAllByFormIdAndQuestionId(formId, questionId);
    }

    @Override
    public void deleteResponsesByFormResponse(UUID formId, Long formResponseId) {
        dateTimeRepository.deleteAllByFormIdAndFormResponseId(formId, formResponseId);
    }
}
