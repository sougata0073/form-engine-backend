package com.sougata.form_data_service.service.responseManager;

import com.sougata.form_data_service.constant.QuestionType;
import com.sougata.form_data_service.dto.question.request.MultipleChoiceGridResponseAddReqDto;
import com.sougata.form_data_service.dto.question.response.MultipleChoiceGridResDto;
import com.sougata.form_data_service.dto.response.individual.MultipleChoiceGridResponseIndividualDto;
import com.sougata.form_data_service.dto.response.question.MultipleChoiceGridResponseQuestionDto;
import com.sougata.form_data_service.dto.response.summary.MultipleChoiceGridResponseSummaryDto;
import com.sougata.form_data_service.model.FormResponse;
import com.sougata.form_data_service.model.MultipleChoiceGrid;
import com.sougata.form_data_service.model.MultipleChoiceGridRow;
import com.sougata.form_data_service.projection.CommonResponseSummaryProjection;
import com.sougata.form_data_service.repository.MultipleChoiceGridRepository;
import com.sougata.form_data_service.repository.QuestionResponseRepository;
import com.sougata.form_data_service.util.IdUtil;
import jakarta.persistence.Tuple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service("MULTIPLE_CHOICE_GRID_RESPONSE_MANAGER")
public class MultipleChoiceGridManager extends ResponseManager<
        MultipleChoiceGridResponseAddReqDto,
        MultipleChoiceGridResponseSummaryDto,
        MultipleChoiceGridResDto,
        MultipleChoiceGridResponseQuestionDto,
        MultipleChoiceGridResponseQuestionDto.Response,
        MultipleChoiceGridResponseQuestionDto.Summary,
        MultipleChoiceGridResponseIndividualDto
        > {

    private final MultipleChoiceGridRepository multipleChoiceGridRepository;

    @Autowired
    public MultipleChoiceGridManager(MultipleChoiceGridRepository multipleChoiceGridRepository, QuestionResponseRepository questionResponseRepository) {
        super(questionResponseRepository);
        this.multipleChoiceGridRepository = multipleChoiceGridRepository;
    }

    @Override
    @Transactional
    public void create(MultipleChoiceGridResponseAddReqDto response, FormResponse formResponse) {
        MultipleChoiceGrid multipleChoiceGrid = new MultipleChoiceGrid();

        var qr = createQuestionResponse(response.getQuestionId(), formResponse);

        var responses = response.getRows().stream().map(r -> {
            var row = new MultipleChoiceGridRow();

            row.setRowId(r.rowId());
            row.setResponseColumnId(r.responseColumnId());
            row.setMultipleChoiceGrid(multipleChoiceGrid);

            return row;
        }).collect(Collectors.toCollection(ArrayList::new));

        multipleChoiceGrid.setQuestionResponse(qr);
        multipleChoiceGrid.setResponses(responses);

        multipleChoiceGridRepository.save(multipleChoiceGrid);
    }

    @Override
    public List<MultipleChoiceGridResponseSummaryDto> getResponseSummaries(
            UUID formId,
            List<MultipleChoiceGridResDto> questionResponses) {

        var responseSummaries = multipleChoiceGridRepository.getResponseSummaries(formId);

        var summaryMap = responseSummaries.stream()
                .collect(Collectors.toMap(
                        CommonResponseSummaryProjection::questionId,
                        Function.identity()
                ));

        var groupedResponses = multipleChoiceGridRepository.getResponseOptionCount(formId)
                .stream()
                .collect(Collectors.groupingBy(
                        t -> t.get("questionId", Long.class),
                        Collectors.groupingBy(
                                t -> t.get("rowId", Long.class)
                        )
                ));

        var result = new ArrayList<MultipleChoiceGridResponseSummaryDto>();

        for (var qr : questionResponses) {

            var dto = new MultipleChoiceGridResponseSummaryDto();

            dto.setQuestionId(qr.getId());
            dto.setQuestion(qr.getQuestion());
            dto.setOrderIndex(qr.getOrderIndex());
            dto.setQuestionType(QuestionType.MULTIPLE_CHOICE_GRID);

            var summary = summaryMap.get(qr.getId());
            dto.setNumberOfResponses(summary == null ? 0L : summary.numberOfResponses());

            var rowResponses = qr.getRows().stream()
                    .map(row -> {

                        var rowTuples = groupedResponses
                                .getOrDefault(qr.getId(), Collections.emptyMap())
                                .getOrDefault(row.id(), Collections.emptyList());

                        Map<Long, Long> columnCountMap = rowTuples.stream()
                                .collect(Collectors.toMap(
                                        t -> t.get("responseColumnId", Long.class),
                                        t -> t.get("responseCount", Long.class)
                                ));

                        var columnResponses = qr.getColumns().stream()
                                .map(column ->
                                        new MultipleChoiceGridResponseSummaryDto.ColumnResponse(
                                                column.id(),
                                                column.column(),
                                                columnCountMap.getOrDefault(column.id(), 0L)
                                        )
                                )
                                .toList();

                        return new MultipleChoiceGridResponseSummaryDto.RowResponse(
                                row.id(),
                                row.row(),
                                columnResponses
                        );
                    })
                    .toList();

            dto.setResponses(rowResponses);

            result.add(dto);
        }

        return result;
    }

    @Override
    public MultipleChoiceGridResponseSummaryDto getResponseSummary(UUID formId, Long questionId, MultipleChoiceGridResDto questionRes, Pageable pageable) {
        var responseSummary = multipleChoiceGridRepository.getResponseSummary(formId, questionId);
        var res = new MultipleChoiceGridResponseSummaryDto();

        res.setQuestionId(questionRes.getId());
        res.setQuestion(questionRes.getQuestion());
        res.setQuestionType(getQuestionType());
        res.setOrderIndex(questionRes.getOrderIndex());
        res.setNumberOfResponses(responseSummary.numberOfResponses());
        res.setResponses(List.of());

        return res;
    }

    @Override
    public MultipleChoiceGridResponseQuestionDto.Summary getResponseByQuestionSummary(UUID formId, MultipleChoiceGridResDto questionResponse) {
        var sum = new MultipleChoiceGridResponseQuestionDto.Summary();

        sum.setRows(questionResponse.getRows());
        sum.setColumns(questionResponse.getColumns());

        return sum;
    }

    @Override
    public MultipleChoiceGridResponseQuestionDto getResponseByQuestion(UUID formId, Long questionId, Map<String, String> extraParams, Pageable pageable) {

        var rowIdString = extraParams.get("rowId");

        if (rowIdString == null) {
            throw new IllegalArgumentException("Row ID is required");
        }

        long rowId;

        try {
            rowId = Long.parseLong(rowIdString);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid row ID: " + rowIdString);
        }

        var grouped = multipleChoiceGridRepository.groupedByResponseRowColumn(
                formId,
                questionId,
                rowId,
                pageable
        );

        var mc = new MultipleChoiceGridResponseQuestionDto();

        var responses = grouped.stream().map(g -> {
            var res = new MultipleChoiceGridResponseQuestionDto.Response();

            res.setQuestionId(questionId);
            res.setQuestionType(getQuestionType());
            res.setColumnId(g.get("columnId", Long.class));
            res.setResponseCount(g.get("responseCount", Long.class));

            var map = new HashMap<String, List<String>>();

            map.put("rowId", List.of(rowIdString));
            map.put("columnId", List.of(res.getColumnId() == null ? "" : res.getColumnId().toString()));

            res.setFormResponsesIdentifier(IdUtil.generateCompressedEncodedId(map));

            return res;
        }).toList();

        mc.setQuestionId(questionId);
        mc.setQuestionType(getQuestionType());
        mc.setRowId(rowId);
        mc.setResponses(responses);

        return mc;
    }

    @Override
    public List<MultipleChoiceGridResponseIndividualDto> getIndividualResponses(UUID formId, Long formResponseId) {
        var responses = multipleChoiceGridRepository.getRowColumnIdsByFormResponse(formId, formResponseId);

        return responses.stream().map(tuple -> {
            var qId = tuple.get("questionId", Long.class);
            var rowIds = tuple.get("rowIds", Long[].class);
            var columnIds = tuple.get("columnIds", Long[].class);

            var res = new MultipleChoiceGridResponseIndividualDto();

            res.setQuestionId(qId);
            res.setQuestionType(getQuestionType());

            var rows = new ArrayList<MultipleChoiceGridResponseIndividualDto.MultipleChoiceGridResponseIndividualDtoRow>();

            for (int i = 0; i < rowIds.length; i++) {
                rows.add(
                        new MultipleChoiceGridResponseIndividualDto.MultipleChoiceGridResponseIndividualDtoRow(
                                rowIds[i], columnIds[i]
                        )
                );
            }

            res.setRows(rows);

            return res;
        }).toList();
    }

    @Override
    public List<Tuple> getFormResponseAndUserIds(UUID formId, Long questionId, String formResponsesIdentifier, Pageable pageable) {
        var map = IdUtil.reconstructCompressedEncodedId(formResponsesIdentifier);

        var rowId = map.get("rowId");
        var columnId = map.get("columnId");

        if (rowId.isEmpty() || columnId.isEmpty()) {
            throw new IllegalArgumentException("Invalid Form Responses Identifier. Identifier: " + formResponsesIdentifier);
        }

        var rowIdResponse = rowId.getFirst() == null ? null :  Long.parseLong(rowId.getFirst());
        var columnIdResponse = columnId.getFirst() == null ? null : Long.parseLong(columnId.getFirst());

        return multipleChoiceGridRepository.getResponseIdsByGroupedResponse(formId, questionId, rowIdResponse, columnIdResponse, pageable);
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.MULTIPLE_CHOICE_GRID;
    }

    @Override
    public void deleteResponsesByQuestion(UUID formId, Long questionId) {
        multipleChoiceGridRepository.deleteAllByFormIdAndQuestionId(formId, questionId);
    }

    @Override
    public void deleteResponsesByFormResponse(UUID formId, Long formResponseId) {
        multipleChoiceGridRepository.deleteAllByFormIdAndFormResponseId(formId, formResponseId);
    }
}
