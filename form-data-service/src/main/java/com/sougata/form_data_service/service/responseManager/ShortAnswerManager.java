package com.sougata.form_data_service.service.responseManager;

import com.sougata.form_data_service.constant.QuestionType;
import com.sougata.form_data_service.dto.question.request.ShortAnswerResponseAddReqDto;
import com.sougata.form_data_service.dto.question.response.ShortAnswerResDto;
import com.sougata.form_data_service.dto.response.individual.ParagraphResponseIndividualDto;
import com.sougata.form_data_service.dto.response.individual.ShortAnswerResponseIndividualDto;
import com.sougata.form_data_service.dto.response.question.ShortAnswerResponseQuestionDto;
import com.sougata.form_data_service.dto.response.summary.ShortAnswerResponseSummaryDto;
import com.sougata.form_data_service.feignClient.AuthServiceFeignClient;
import com.sougata.form_data_service.model.FormResponse;
import com.sougata.form_data_service.model.ShortAnswer;
import com.sougata.form_data_service.repository.FormResponseRepository;
import com.sougata.form_data_service.repository.QuestionResponseRepository;
import com.sougata.form_data_service.repository.ShortAnswerRepository;
import com.sougata.form_data_service.util.IdUtil;
import com.sougata.form_data_service.util.StringUtil;
import jakarta.persistence.Tuple;
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
        ShortAnswerResponseQuestionDto.Summary,
        ShortAnswerResponseIndividualDto
        > {

    private final ShortAnswerRepository shortAnswerRepository;
    private final AuthServiceFeignClient authServiceFeignClient;
    private final QuestionResponseRepository questionResponseRepository;

    @Autowired
    public ShortAnswerManager(ShortAnswerRepository shortAnswerRepository, QuestionResponseRepository questionResponseRepository, FormResponseRepository formResponseRepository, AuthServiceFeignClient authServiceFeignClient) {
        super(questionResponseRepository);
        this.shortAnswerRepository = shortAnswerRepository;
        this.questionResponseRepository = questionResponseRepository;
        this.authServiceFeignClient = authServiceFeignClient;
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

//                                    var texts = shortAnswerRepository.getResponseTexts(formId, rs.questionId(), Pageable.ofSize(20));
//
//                                    sa.setResponses(texts);
                                    sa.setResponses(List.of());

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
    public ShortAnswerResponseSummaryDto getResponseSummary(UUID formId, Long questionId, ShortAnswerResDto questionRes, Pageable pageable) {
        var responseSummary = shortAnswerRepository.getResponseSummary(formId, questionId);
        var texts = shortAnswerRepository.getResponseTexts(formId, questionId, pageable);

        var sa = new ShortAnswerResponseSummaryDto();

        sa.setQuestionId(questionRes.getId());
        sa.setQuestion(questionRes.getQuestion());
        sa.setOrderIndex(questionRes.getOrderIndex());
        sa.setNumberOfResponses(responseSummary.numberOfResponses());
        sa.setQuestionType(getQuestionType());
        sa.setResponses(texts);

        return sa;
    }

    @Override
    public ShortAnswerResponseQuestionDto.Summary getResponseByQuestionSummary(UUID formId, ShortAnswerResDto questionResponse) {
        return new ShortAnswerResponseQuestionDto.Summary();
    }

    @Override
    public ShortAnswerResponseQuestionDto getResponseByQuestion(UUID formId, Long questionId, Map<String, String> extraParams, Pageable pageable) {

        var grouped = shortAnswerRepository.groupedByText(formId, questionId, pageable);

        var sa = new ShortAnswerResponseQuestionDto();

        var responses = grouped.stream().map(g -> {
            var res = new ShortAnswerResponseQuestionDto.Response();

            res.setQuestionId(questionId);
            res.setQuestionType(getQuestionType());
            res.setText(g.get("text", String.class));
            res.setResponseCount(g.get("responseCount", Long.class));

            var map = new HashMap<String, List<String>>();

            map.put("text", List.of(StringUtil.emptyIfNull(res.getText())));

            res.setFormResponsesIdentifier(IdUtil.generateCompressedEncodedId(map));

            return res;
        }).toList();

        sa.setQuestionId(questionId);
        sa.setQuestionType(getQuestionType());
        sa.setResponses(responses);

        return sa;
    }

    @Override
    public List<ShortAnswerResponseIndividualDto> getIndividualResponses(UUID formId, Long formResponseId) {
        var responses = shortAnswerRepository.getTextsByFormResponse(formId, formResponseId);

        return responses.stream().map(tuple -> {
            var qId = tuple.get("questionId", Long.class);
            var text = tuple.get("text", String.class);

            var res = new ShortAnswerResponseIndividualDto();

            res.setQuestionId(qId);
            res.setQuestionType(getQuestionType());
            res.setText(text);

            return res;
        }).toList();
    }

    @Override
    public List<Tuple> getFormResponseAndUserIds(UUID formId, Long questionId, String formResponsesIdentifier, Pageable pageable) {
        var map = IdUtil.reconstructCompressedEncodedId(formResponsesIdentifier);

        var text = map.get("text");

        if (text.isEmpty()) {
            throw new IllegalArgumentException("Invalid Form Responses Identifier. Identifier: " + formResponsesIdentifier);
        }

        var groupedResponse = text.getFirst();

        return shortAnswerRepository.getResponseIdsByGroupedResponse(formId, questionId, groupedResponse, pageable);
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.SHORT_ANSWER;
    }

    @Override
    public void deleteResponsesByQuestion(UUID formId, Long questionId) {
        shortAnswerRepository.deleteAllByFormIdAndQuestionId(formId, questionId);
    }

    @Override
    public void deleteResponsesByFormResponse(UUID formId, Long formResponseId) {
        shortAnswerRepository.deleteAllByFormIdAndFormResponseId(formId, formResponseId);
    }
}
