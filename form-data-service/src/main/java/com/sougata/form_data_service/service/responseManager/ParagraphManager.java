package com.sougata.form_data_service.service.responseManager;

import com.sougata.form_data_service.constant.QuestionType;
import com.sougata.form_data_service.dto.question.request.ParagraphResponseAddReqDto;
import com.sougata.form_data_service.dto.question.response.ParagraphResDto;
import com.sougata.form_data_service.dto.response.question.ParagraphResponseQuestionDto;
import com.sougata.form_data_service.dto.response.summary.ParagraphResponseSummaryDto;
import com.sougata.form_data_service.model.FormResponse;
import com.sougata.form_data_service.model.Paragraph;
import com.sougata.form_data_service.repository.ParagraphRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service("PARAGRAPH_RESPONSE_MANAGER")
public class ParagraphManager extends ResponseManager<ParagraphResponseAddReqDto, ParagraphResponseSummaryDto, ParagraphResDto, ParagraphResponseQuestionDto> {

    private final ParagraphRepository paragraphRepository;

    @Autowired
    public ParagraphManager(ParagraphRepository paragraphRepository) {
        this.paragraphRepository = paragraphRepository;
    }

    @Override
    public void create(ParagraphResponseAddReqDto response, FormResponse formResponse) {
        Paragraph paragraph = new Paragraph();
        paragraph.setText(response.getText());
        paragraph.setQuestionId(response.getQuestionId());
        paragraph.setFormResponse(formResponse);

        paragraphRepository.save(paragraph);
    }

    @Override
    public List<ParagraphResponseSummaryDto> getResponseSummaries(UUID formId, List<ParagraphResDto> questionResponses) {
        var responseSummaries = paragraphRepository.getResponseSummaries(formId);
        var result = new ArrayList<ParagraphResponseSummaryDto>();

        var responseTextMap = paragraphRepository.getResponseTexts(formId)
                .stream().collect(Collectors.groupingBy(e -> e.get("questionId", Long.class)));

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
                                    p.setQuestionType(QuestionType.PARAGRAPH);
                                    p.setResponses(
                                            responseTextMap.get(rs.questionId())
                                                    .stream().map(tuple -> tuple.get("text", String.class)).toList()
                                    );

                                    return p;
                                })
                                .findFirst()
                                .orElseGet(() -> {
                                    var p = new ParagraphResponseSummaryDto();

                                    p.setQuestionId(qr.getId());
                                    p.setQuestion(qr.getQuestion());
                                    p.setOrderIndex(qr.getOrderIndex());
                                    p.setNumberOfResponses(0L);
                                    p.setQuestionType(QuestionType.PARAGRAPH);
                                    p.setResponses(List.of());

                                    return p;
                                })
                ));

        return result;
    }

    @Override
    public ParagraphResponseQuestionDto getResponseByQuestion(UUID formId, ParagraphResDto questionRes) {
        var grouped = paragraphRepository.groupedByText(formId, questionRes.getId());

        var totalResponseCount = grouped.stream()
                .mapToLong(g -> g.get("responseCount", Long.class).intValue())
                .sum();

        var p = new ParagraphResponseQuestionDto();

        var responses = grouped.stream().map(g -> new ParagraphResponseQuestionDto.Response(
                g.get("text", String.class),
                g.get("responseCount", Long.class).intValue(),
                Arrays.stream(g.get("responseIds", Long[].class)).map(Object::toString).toList()
        )).toList();

        p.setQuestionId(questionRes.getId());
        p.setQuestion(questionRes.getQuestion());
        p.setQuestionType(questionRes.getQuestionType());
        p.setResponses(responses);
        p.setTotalResponseCount(totalResponseCount);

        return p;
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.PARAGRAPH;
    }

    @Override
    public void deleteResponses(UUID formId, Long questionId) {
        var entities = paragraphRepository.findByFormIdAndQuestionId(formId, questionId);

        paragraphRepository.deleteAll(entities);
    }
}
