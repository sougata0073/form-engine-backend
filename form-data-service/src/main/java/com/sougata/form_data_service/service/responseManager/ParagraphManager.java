package com.sougata.form_data_service.service.responseManager;

import com.sougata.form_data_service.constant.QuestionType;
import com.sougata.form_data_service.dto.question.request.ParagraphResponseAddReqDto;
import com.sougata.form_data_service.dto.question.response.ParagraphResDto;
import com.sougata.form_data_service.dto.response.question.ParagraphResponseQuestionDto;
import com.sougata.form_data_service.dto.response.summary.ParagraphResponseSummaryDto;
import com.sougata.form_data_service.model.FormResponse;
import com.sougata.form_data_service.model.Paragraph;
import com.sougata.form_data_service.repository.ParagraphRepository;
import com.sougata.form_data_service.repository.QuestionResponseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service("PARAGRAPH_RESPONSE_MANAGER")
public class ParagraphManager extends ResponseManager<
        ParagraphResponseAddReqDto,
        ParagraphResponseSummaryDto,
        ParagraphResDto,
        ParagraphResponseQuestionDto,
        ParagraphResponseQuestionDto.Response,
        ParagraphResponseQuestionDto.Summary
        > {

    private final ParagraphRepository paragraphRepository;

    @Autowired
    public ParagraphManager(ParagraphRepository paragraphRepository, QuestionResponseRepository questionResponseRepository) {
        super(questionResponseRepository);
        this.paragraphRepository = paragraphRepository;
    }

    @Override
    @Transactional
    public void create(ParagraphResponseAddReqDto response, FormResponse formResponse) {
        Paragraph paragraph = new Paragraph();

        var qr = createQuestionResponse(response.getQuestionId(), formResponse);

        paragraph.setText(response.getText());
        paragraph.setQuestionResponse(qr);

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
    public ParagraphResponseQuestionDto.Summary getResponseByQuestionSummary(UUID formId, ParagraphResDto questionResponse) {
        var sum = new ParagraphResponseQuestionDto.Summary();

        sum.setQuestionId(questionResponse.getId());
        sum.setQuestion(questionResponse.getQuestion());
        sum.setQuestionType(questionResponse.getQuestionType());
        sum.setTotalResponseCount(getTotalResponseCount(formId, questionResponse.getId()));
        sum.setDistinctResponseCount(paragraphRepository.getDistinctResponseCount(formId, questionResponse.getId()));

        return sum;
    }

    @Override
    public ParagraphResponseQuestionDto getResponseByQuestion(UUID formId, Long questionId, Map<String, String> extraParams, Pageable pageable) {
        var grouped = paragraphRepository.groupedByText(formId, questionId, pageable);

        var p = new ParagraphResponseQuestionDto();

        var responses = grouped.stream().map(g -> {
            var res = new ParagraphResponseQuestionDto.Response();

            res.setText(g.get("text", String.class));
            res.setResponseCount(g.get("responseCount", Long.class));
            res.setResponseIds(Arrays.stream(g.get("responseIds", Long[].class)).map(Object::toString).toList());

            return res;
        }).toList();

        p.setResponses(responses);

        return p;
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.PARAGRAPH;
    }

    @Override
    public void deleteResponses(UUID formId, Long questionId) {
        paragraphRepository.deleteAllByFormIdAndQuestionId(formId, questionId);
    }
}
