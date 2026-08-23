package com.sougata.form_data_service.service.responseManager;

import com.sougata.form_data_service.constant.QuestionType;
import com.sougata.form_data_service.dto.question.request.ParagraphResponsePutReqDto;
import com.sougata.form_data_service.dto.question.response.ParagraphDetailsDto;
import com.sougata.form_data_service.dto.response.individual.ParagraphResponseIndividualDto;
import com.sougata.form_data_service.dto.response.question.ParagraphResponseQuestionDto;
import com.sougata.form_data_service.dto.response.summary.ParagraphResponseSummaryDto;
import com.sougata.form_data_service.feignClient.AuthServiceFeignClient;
import com.sougata.form_data_service.model.FormResponse;
import com.sougata.form_data_service.model.Paragraph;
import com.sougata.form_data_service.repository.FormResponseRepository;
import com.sougata.form_data_service.repository.ParagraphRepository;
import com.sougata.form_data_service.repository.QuestionResponseRepository;
import com.sougata.form_data_service.util.IdUtil;
import com.sougata.form_data_service.util.StringUtil;
import jakarta.persistence.Tuple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service("PARAGRAPH_RESPONSE_MANAGER")
public class ParagraphManager extends ResponseManager<
        ParagraphResponsePutReqDto,
        ParagraphResponseSummaryDto,
        ParagraphDetailsDto,
        ParagraphResponseQuestionDto,
        ParagraphResponseQuestionDto.Response,
        ParagraphResponseQuestionDto.Summary,
        ParagraphResponseIndividualDto
        > {

    private final ParagraphRepository paragraphRepository;

    @Autowired
    public ParagraphManager(ParagraphRepository paragraphRepository, QuestionResponseRepository questionResponseRepositor, FormResponseRepository formResponseRepository, AuthServiceFeignClient authServiceFeignClient) {
        super(questionResponseRepositor);
        this.paragraphRepository = paragraphRepository;
    }

    @Override
    @Transactional
    public void create(ParagraphResponsePutReqDto response, FormResponse formResponse) {
        Paragraph paragraph = new Paragraph();

        var qr = createQuestionResponse(response.getQuestionId(), formResponse);

        paragraph.setText(response.getText());
        paragraph.setQuestionResponse(qr);

        paragraphRepository.save(paragraph);
    }

    @Override
    public List<ParagraphResponseSummaryDto> getResponseSummaries(UUID formId, List<ParagraphDetailsDto> questionResponses) {
        var responseSummaries = paragraphRepository.getResponseSummaries(formId);
        var result = new ArrayList<ParagraphResponseSummaryDto>();

        questionResponses.forEach(qr ->
                result.add(
                        responseSummaries.stream()
                                .filter(rs -> Objects.equals(rs.questionId(), qr.getId()))
                                .map(rs -> {
                                    var p = new ParagraphResponseSummaryDto();

                                    p.setQuestionId(qr.getId());
                                    p.setQuestion(qr.getQuestion());
                                    p.setOrderIndex(qr.getOrderIndex());
                                    p.setNumberOfResponses(rs.numberOfResponses());
                                    p.setQuestionType(getQuestionType());

//                                    var texts = paragraphRepository.getResponseTexts(formId, rs.questionId(), Pageable.ofSize(20));
//
//                                    p.setResponses(texts);
                                    p.setResponses(List.of());

                                    return p;
                                })
                                .findFirst()
                                .orElseGet(() -> {
                                    var p = new ParagraphResponseSummaryDto();

                                    p.setQuestionId(qr.getId());
                                    p.setQuestion(qr.getQuestion());
                                    p.setOrderIndex(qr.getOrderIndex());
                                    p.setNumberOfResponses(0L);
                                    p.setQuestionType(getQuestionType());
                                    p.setResponses(List.of());

                                    return p;
                                })
                ));

        return result;
    }

    @Override
    public ParagraphResponseSummaryDto getResponseSummary(UUID formId, Long questionId, ParagraphDetailsDto questionRes, Pageable pageable) {
        var responseSummary = paragraphRepository.getResponseSummary(formId, questionId);
        var texts = paragraphRepository.getResponseTexts(questionId, pageable);

        var p = new ParagraphResponseSummaryDto();

        p.setQuestionId(questionRes.getId());
        p.setQuestion(questionRes.getQuestion());
        p.setOrderIndex(questionRes.getOrderIndex());
        p.setNumberOfResponses(responseSummary.numberOfResponses());
        p.setQuestionType(getQuestionType());
        p.setResponses(texts);

        return p;
    }

    @Override
    public ParagraphResponseQuestionDto.Summary getResponseByQuestionSummary(UUID formId, ParagraphDetailsDto questionResponse) {
        return new ParagraphResponseQuestionDto.Summary();
    }

    @Override
    public ParagraphResponseQuestionDto getResponseByQuestion(UUID formId, Long questionId, Map<String, String> extraParams, Pageable pageable) {
        var grouped = paragraphRepository.groupedByText(questionId, pageable);

        var p = new ParagraphResponseQuestionDto();

        var responses = grouped.stream().map(g -> {
            var res = new ParagraphResponseQuestionDto.Response();

            res.setQuestionId(questionId);
            res.setQuestionType(getQuestionType());
            res.setText(g.get("text", String.class));
            res.setResponseCount(g.get("responseCount", Long.class));

            var map = new HashMap<String, List<String>>();

            map.put("text", List.of(StringUtil.emptyIfNull(res.getText())));

            res.setFormResponsesIdentifier(IdUtil.generateCompressedEncodedId(map));

            return res;
        }).toList();

        p.setQuestionId(questionId);
        p.setQuestionType(getQuestionType());
        p.setResponses(responses);

        return p;
    }

    @Override
    public List<ParagraphResponseIndividualDto> getIndividualResponses(UUID formId, Long formResponseId) {
        var responses = paragraphRepository.getTextsByFormResponse(formResponseId);

        return responses.stream().map(tuple -> {
            var qId = tuple.get("questionId", Long.class);
            var text = tuple.get("text", String.class);

            var res = new ParagraphResponseIndividualDto();

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

        return paragraphRepository.getResponseIdsByGroupedResponse(questionId, groupedResponse, pageable);
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.PARAGRAPH;
    }

    @Override
    public void deleteResponsesByQuestion(UUID formId, Long questionId) {
        paragraphRepository.deleteAllByFormIdAndQuestionId(formId, questionId);
    }

    @Override
    public void deleteResponsesByFormResponse(UUID formId, Long formResponseId) {
        paragraphRepository.deleteAllByFormIdAndFormResponseId(formId, formResponseId);
    }
}
