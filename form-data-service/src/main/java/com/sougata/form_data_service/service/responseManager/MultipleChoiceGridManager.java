package com.sougata.form_data_service.service.responseManager;

import com.sougata.form_data_service.constant.QuestionType;
import com.sougata.form_data_service.dto.question.request.MultipleChoiceGridResponseAddReqDto;
import com.sougata.form_data_service.dto.question.response.MultipleChoiceGridResDto;
import com.sougata.form_data_service.dto.response.question.MultipleChoiceGridResponseQuestionDto;
import com.sougata.form_data_service.dto.response.summary.MultipleChoiceGridResponseSummaryDto;
import com.sougata.form_data_service.model.FormResponse;
import com.sougata.form_data_service.model.MultipleChoiceGrid;
import com.sougata.form_data_service.model.MultipleChoiceGridRow;
import com.sougata.form_data_service.projection.responseSummary.CommonResponseSummaryProjection;
import com.sougata.form_data_service.repository.MultipleChoiceGridRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service("MULTIPLE_CHOICE_GRID_RESPONSE_MANAGER")
public class MultipleChoiceGridManager extends ResponseManager<MultipleChoiceGridResponseAddReqDto, MultipleChoiceGridResponseSummaryDto, MultipleChoiceGridResDto, MultipleChoiceGridResponseQuestionDto> {

    private final MultipleChoiceGridRepository multipleChoiceGridRepository;

    @Autowired
    public MultipleChoiceGridManager(MultipleChoiceGridRepository multipleChoiceGridRepository) {
        this.multipleChoiceGridRepository = multipleChoiceGridRepository;
    }

    @Override
    public void create(MultipleChoiceGridResponseAddReqDto response, FormResponse formResponse) {
        MultipleChoiceGrid multipleChoiceGrid = new MultipleChoiceGrid();

        multipleChoiceGrid.setQuestionId(response.getQuestionId());
        multipleChoiceGrid.setFormResponse(formResponse);

        var responses = response.getRows().stream().map(r -> {
            var row = new MultipleChoiceGridRow();

            row.setRowId(r.rowId());
            row.setResponseColumnId(r.responseColumnId());
            row.setMultipleChoiceGrid(multipleChoiceGrid);

            return row;
        }).collect(Collectors.toCollection(ArrayList::new));

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
    public MultipleChoiceGridResponseQuestionDto getResponseByQuestion(UUID formId, MultipleChoiceGridResDto questionRes) {
        return null;
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.MULTIPLE_CHOICE_GRID;
    }

    @Override
    public void deleteResponses(UUID formId, Long questionId) {
        var entities = multipleChoiceGridRepository.findByFormIdAndQuestionId(formId, questionId);

        multipleChoiceGridRepository.deleteAll(entities);
    }
}
