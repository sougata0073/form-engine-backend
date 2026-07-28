package com.sougata.form_data_service.service.responseManager;

import com.sougata.form_data_service.constant.QuestionType;
import com.sougata.form_data_service.dto.question.request.DateResponseAddReqDto;
import com.sougata.form_data_service.dto.question.response.DateResDto;
import com.sougata.form_data_service.dto.response.question.DateResponseQuestionDto;
import com.sougata.form_data_service.dto.response.summary.DateResponseSummaryDto;
import com.sougata.form_data_service.model.Date;
import com.sougata.form_data_service.model.FormResponse;
import com.sougata.form_data_service.repository.DateRepository;
import com.sougata.form_data_service.repository.QuestionResponseRepository;
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
        DateResponseQuestionDto.Summary
        > {

    private final DateRepository dateRepository;

    @Autowired
    public DateManager(DateRepository dateRepository, QuestionResponseRepository questionResponseRepository) {
        super(questionResponseRepository);
        this.dateRepository = dateRepository;
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

            res.setDate(g.get("date", Instant.class));
            res.setResponseCount(g.get("responseCount", Long.class));
            res.setResponseIds(Arrays.stream(g.get("responseIds", Long[].class)).map(Object::toString).toList());
            
            return res;

        }).toList();

        d.setResponses(responses);

        return d;
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
