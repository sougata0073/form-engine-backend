package com.sougata.form_data_service.service.responseManager;

import com.sougata.form_data_service.constant.QuestionType;
import com.sougata.form_data_service.dto.question.request.TickBoxGridResponseAddReqDto;
import com.sougata.form_data_service.dto.question.response.TickBoxGridResDto;
import com.sougata.form_data_service.dto.response.question.TickBoxGridResponseQuestionDto;
import com.sougata.form_data_service.dto.response.summary.TickBoxGridResponseSummaryDto;
import com.sougata.form_data_service.model.FormResponse;
import com.sougata.form_data_service.model.TickBoxGrid;
import com.sougata.form_data_service.model.TickBoxGridColumn;
import com.sougata.form_data_service.model.TickBoxGridRow;
import com.sougata.form_data_service.projection.CommonResponseSummaryProjection;
import com.sougata.form_data_service.repository.QuestionResponseRepository;
import com.sougata.form_data_service.repository.TickBoxGridRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service("TICK_BOX_GRID_RESPONSE_MANAGER")
public class TickBoxGridManager extends ResponseManager<
        TickBoxGridResponseAddReqDto,
        TickBoxGridResponseSummaryDto,
        TickBoxGridResDto,
        TickBoxGridResponseQuestionDto,
        TickBoxGridResponseQuestionDto.Response,
        TickBoxGridResponseQuestionDto.Summary
        > {

    private final TickBoxGridRepository tickBoxGridRepository;

    @Autowired
    public TickBoxGridManager(TickBoxGridRepository tickBoxGridRepository, QuestionResponseRepository questionResponseRepository) {
        super(questionResponseRepository);
        this.tickBoxGridRepository = tickBoxGridRepository;
    }

    @Override
    @Transactional
    public void create(TickBoxGridResponseAddReqDto response, FormResponse formResponse) {
        TickBoxGrid tickBoxGrid = new TickBoxGrid();

        var qr = createQuestionResponse(response.getQuestionId(), formResponse);

        var responses = response.getRows().stream().map(r -> {
            var row = new TickBoxGridRow();

            var columns = r.responseColumnIds().stream().map(c -> {
                var column = new TickBoxGridColumn();

                column.setResponseOptionId(c);
                column.setTickBoxGridRow(row);

                return column;
            }).collect(Collectors.toCollection(ArrayList::new));

            row.setRowId(r.rowId());
            row.setResponses(columns);
            row.setTickBoxGrid(tickBoxGrid);

            return row;
        }).collect(Collectors.toCollection(ArrayList::new));

        tickBoxGrid.setQuestionResponse(qr);
        tickBoxGrid.setResponses(responses);

        tickBoxGridRepository.save(tickBoxGrid);
    }

    @Override
    public List<TickBoxGridResponseSummaryDto> getResponseSummaries(
            UUID formId,
            List<TickBoxGridResDto> questionResponses) {

        var responseSummaries = tickBoxGridRepository.getResponseSummaries(formId);

        var summaryMap = responseSummaries.stream()
                .collect(Collectors.toMap(
                        CommonResponseSummaryProjection::questionId,
                        Function.identity()
                ));

        var groupedResponses = tickBoxGridRepository.getResponseOptionCount(formId)
                .stream()
                .collect(Collectors.groupingBy(
                        t -> t.get("questionId", Long.class),
                        Collectors.groupingBy(
                                t -> t.get("rowId", Long.class)
                        )
                ));

        var result = new ArrayList<TickBoxGridResponseSummaryDto>();

        for (var qr : questionResponses) {

            var dto = new TickBoxGridResponseSummaryDto();

            dto.setQuestionId(qr.getId());
            dto.setQuestion(qr.getQuestion());
            dto.setOrderIndex(qr.getOrderIndex());
            dto.setQuestionType(QuestionType.TICK_BOX_GRID);

            var summary = summaryMap.get(qr.getId());
            dto.setNumberOfResponses(summary == null ? 0L : summary.numberOfResponses());

            var rowResponses = qr.getRows().stream()
                    .map(row -> {

                        var rowTuples = groupedResponses
                                .getOrDefault(qr.getId(), Collections.emptyMap())
                                .getOrDefault(row.id(), Collections.emptyList());

                        Map<Long, Long> optionCountMap = rowTuples.stream()
                                .collect(Collectors.toMap(
                                        t -> t.get("responseOptionId", Long.class),
                                        t -> t.get("responseCount", Long.class)
                                ));

                        var columnResponses = qr.getColumns().stream()
                                .map(column ->
                                        new TickBoxGridResponseSummaryDto.ColumnResponse(
                                                column.id(),
                                                column.column(),
                                                optionCountMap.getOrDefault(column.id(), 0L)
                                        )
                                )
                                .toList();

                        return new TickBoxGridResponseSummaryDto.RowResponse(
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
    public TickBoxGridResponseQuestionDto.Summary getResponseByQuestionSummary(UUID formId, TickBoxGridResDto questionResponse) {
        var sum = new TickBoxGridResponseQuestionDto.Summary();

        sum.setQuestionId(questionResponse.getId());
        sum.setQuestion(questionResponse.getQuestion());
        sum.setQuestionType(questionResponse.getQuestionType());
        sum.setRows(questionResponse.getRows());
        sum.setColumns(questionResponse.getColumns());

        var responseCount = getTotalResponseCount(formId, questionResponse.getId());

        sum.setTotalResponseCount(responseCount);
        sum.setDistinctResponseCount(responseCount);

        return sum;
    }

    @Override
    public TickBoxGridResponseQuestionDto getResponseByQuestion(UUID formId, Long questionId, Map<String, String> extraParams, Pageable pageable) {

        var rowIdString = extraParams.get("rowId");

        if (rowIdString == null) {
            throw new IllegalArgumentException("Row ID is required");
        }

        var rowId = Long.parseLong(rowIdString);

        var grouped = tickBoxGridRepository.groupedByResponseRowColumn(
                formId,
                questionId,
                rowId,
                pageable
        );

        var tb = new TickBoxGridResponseQuestionDto();

        var responses = grouped.stream().map(g -> {
            var res = new TickBoxGridResponseQuestionDto.Response();

            res.setColumnIds(Arrays.stream(g.get("columnIds", Long[].class)).map(Object::toString).toList());
            res.setResponseCount(g.get("responseCount", Long.class));
            res.setResponseIds(Arrays.stream(g.get("responseIds", Long[].class)).map(Object::toString).toList());

            return res;
        }).toList();

        tb.setRowId(rowId);
        tb.setResponses(responses);

        return tb;
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.TICK_BOX_GRID;
    }

    @Override
    public void deleteResponses(UUID formId, Long questionId) {
        tickBoxGridRepository.deleteAllByFormIdAndQuestionId(formId, questionId);
    }
}
