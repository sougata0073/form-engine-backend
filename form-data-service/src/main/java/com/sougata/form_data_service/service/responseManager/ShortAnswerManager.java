package com.sougata.form_data_service.service.responseManager;

import com.sougata.form_data_service.constant.QuestionType;
import com.sougata.form_data_service.dto.question.request.ShortAnswerResponseAddReqDto;
import com.sougata.form_data_service.dto.question.response.ShortAnswerResDto;
import com.sougata.form_data_service.dto.response.question.ShortAnswerResponseQuestionDto;
import com.sougata.form_data_service.dto.response.summary.ShortAnswerResponseSummaryDto;
import com.sougata.form_data_service.model.FormResponse;
import com.sougata.form_data_service.model.ShortAnswer;
import com.sougata.form_data_service.repository.QuestionResponseRepository;
import com.sougata.form_data_service.repository.ShortAnswerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service("SHORT_ANSWER_RESPONSE_MANAGER")
public class ShortAnswerManager extends ResponseManager<
        ShortAnswerResponseAddReqDto,
        ShortAnswerResponseSummaryDto,
        ShortAnswerResDto,
        ShortAnswerResponseQuestionDto,
        ShortAnswerResponseQuestionDto.Response,
        ShortAnswerResponseQuestionDto.Summary
        > {

    private final ShortAnswerRepository shortAnswerRepository;

    @Autowired
    public ShortAnswerManager(ShortAnswerRepository shortAnswerRepository, QuestionResponseRepository questionResponseRepository) {
        super(questionResponseRepository);
        this.shortAnswerRepository = shortAnswerRepository;
    }

    @Override
    @Transactional
    public void create(ShortAnswerResponseAddReqDto response, FormResponse formResponse) {
        ShortAnswer shortAnswer = new ShortAnswer();

        var qr = createQuestionResponse(response.getQuestionId(), formResponse);

        shortAnswer.setText(response.getText());
        shortAnswer.setQuestionResponse(qr);

        shortAnswerRepository.save(shortAnswer);
    }

    @Override
    public List<ShortAnswerResponseSummaryDto> getResponseSummaries(UUID formId, List<ShortAnswerResDto> questionResponses) {
        var responseSummaries = shortAnswerRepository.getResponseSummaries(formId);
        var result = new ArrayList<ShortAnswerResponseSummaryDto>();

        var responseTextMap = shortAnswerRepository.getResponseTexts(formId)
                .stream().collect(Collectors.groupingBy(e -> e.get("questionId", Long.class)));

        questionResponses.forEach(qr ->
                result.add(
                        responseSummaries.stream()
                                .filter(rs -> Objects.equals(rs.questionId(), qr.getId()))
                                .map(rs -> {
                                    var sa = new ShortAnswerResponseSummaryDto();

                                    sa.setQuestionId(qr.getId());
                                    sa.setQuestion(qr.getQuestion());
                                    sa.setOrderIndex(qr.getOrderIndex());
                                    sa.setNumberOfResponses(rs.numberOfResponses());
                                    sa.setQuestionType(QuestionType.SHORT_ANSWER);
                                    sa.setResponses(
                                            responseTextMap.get(rs.questionId())
                                                    .stream().map(tuple -> tuple.get("text", String.class)).toList()
                                    );

                                    return sa;
                                })
                                .findFirst()
                                .orElseGet(() -> {
                                    var sa = new ShortAnswerResponseSummaryDto();

                                    sa.setQuestionId(qr.getId());
                                    sa.setQuestion(qr.getQuestion());
                                    sa.setOrderIndex(qr.getOrderIndex());
                                    sa.setNumberOfResponses(0L);
                                    sa.setQuestionType(QuestionType.SHORT_ANSWER);
                                    sa.setResponses(List.of());

                                    return sa;
                                })
                ));

        return result;
    }

    @Override
    public ShortAnswerResponseQuestionDto.Summary getResponseByQuestionSummary(UUID formId, ShortAnswerResDto questionResponse) {
        var sum = new ShortAnswerResponseQuestionDto.Summary();

        sum.setQuestionId(questionResponse.getId());
        sum.setQuestion(questionResponse.getQuestion());
        sum.setQuestionType(questionResponse.getQuestionType());
        sum.setTotalResponseCount(getTotalResponseCount(formId, questionResponse.getId()));
        sum.setDistinctResponseCount(shortAnswerRepository.getDistinctResponseCount(formId, questionResponse.getId()));

        return sum;
    }

    @Override
    public ShortAnswerResponseQuestionDto getResponseByQuestion(UUID formId, Long questionId, Map<String, String> extraParams, Pageable pageable) {

        var grouped = shortAnswerRepository.groupedByText(formId, questionId, pageable);

        var sa = new ShortAnswerResponseQuestionDto();

        var responses = grouped.stream().map(g -> {
            var res = new ShortAnswerResponseQuestionDto.Response();

            res.setText(g.get("text", String.class));
            res.setResponseCount(g.get("responseCount", Long.class));
            res.setResponseIds(Arrays.stream(g.get("responseIds", Long[].class)).map(Object::toString).toList());

            return res;
        }).toList();

        sa.setResponses(responses);

        return sa;
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.SHORT_ANSWER;
    }

    @Override
    public void deleteResponses(UUID formId, Long questionId) {
        shortAnswerRepository.deleteAllByFormIdAndQuestionId(formId, questionId);
    }
}
