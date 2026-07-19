package com.sougata.form_data_service.service.responseManager;

import com.sougata.form_data_service.constant.QuestionType;
import com.sougata.form_data_service.dto.question.request.ShortAnswerResponseAddReqDto;
import com.sougata.form_data_service.dto.response.question.ShortAnswerResponseQuestionDto;
import com.sougata.form_data_service.dto.response.summary.ShortAnswerResponseSummaryDto;
import com.sougata.form_data_service.form_schema.dto.questionSchema.response.ShortAnswerResDto;
import com.sougata.form_data_service.model.FormResponse;
import com.sougata.form_data_service.model.ShortAnswer;
import com.sougata.form_data_service.repository.ShortAnswerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service("SHORT_ANSWER_RESPONSE_MANAGER")
public class ShortAnswerManager
        extends ResponseManager<ShortAnswerResponseAddReqDto, ShortAnswerResponseSummaryDto, ShortAnswerResDto, ShortAnswerResponseQuestionDto> {

    private final ShortAnswerRepository shortAnswerRepository;

    @Autowired
    public ShortAnswerManager(ShortAnswerRepository shortAnswerRepository) {
        this.shortAnswerRepository = shortAnswerRepository;
    }

    @Override
    public void create(ShortAnswerResponseAddReqDto response, FormResponse formResponse) {
        ShortAnswer shortAnswer = new ShortAnswer();
        shortAnswer.setText(response.getText());
        shortAnswer.setQuestionId(response.getQuestionId());
        shortAnswer.setFormResponse(formResponse);

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
    public ShortAnswerResponseQuestionDto getResponseByQuestion(UUID formId, ShortAnswerResDto questionRes) {
        var grouped = shortAnswerRepository.groupedByText(formId, questionRes.getId());

        var sa = new ShortAnswerResponseQuestionDto();

        var responses = grouped.stream().map(g -> new ShortAnswerResponseQuestionDto.Response(
                g.get("text", String.class),
                g.get("responseCount", Long.class).intValue(),
                Arrays.stream(g.get("responseIds", Long[].class)).map(Object::toString).toList()
        )).toList();

        sa.setQuestionId(questionRes.getId());
        sa.setQuestion(questionRes.getQuestion());
        sa.setQuestionType(questionRes.getQuestionType());
        sa.setResponses(responses);

        return sa;
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.SHORT_ANSWER;
    }

    @Override
    public void deleteResponses(UUID formId, Long questionId) {
        var entities = shortAnswerRepository.findByFormIdAndQuestionId(formId, questionId);

        shortAnswerRepository.deleteAll(entities);
    }
}
