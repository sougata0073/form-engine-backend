package com.sougata.form_data_service.form_schema.service.questionSchemaManager;

import com.sougata.form_data_service.constant.QuestionType;
import com.sougata.form_data_service.dto.question.request.MultipleChoiceGridResponseAddReqDto;
import com.sougata.form_data_service.form_schema.dto.questionSchema.response.MultipleChoiceGridResDto;
import com.sougata.form_data_service.form_schema.exception.QuestionSchemaNotFoundException;
import com.sougata.form_data_service.form_schema.exception.ResponseValidationException;
import com.sougata.form_data_service.form_schema.model.MultipleChoiceGridColumnSchema;
import com.sougata.form_data_service.form_schema.model.MultipleChoiceGridRowSchema;
import com.sougata.form_data_service.form_schema.model.MultipleChoiceGridSchema;
import com.sougata.form_data_service.form_schema.repository.MultipleChoiceGridSchemaRepository;
import com.sougata.form_data_service.form_schema.service.QuestionSchemaManager;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

@Service("MULTIPLE_CHOICE_GRID_QUESTION_SCHEMA_MANAGER")
public class MultipleChoiceGridSchemaManager extends QuestionSchemaManager<MultipleChoiceGridSchema, MultipleChoiceGridResDto, MultipleChoiceGridResponseAddReqDto> {

    private final MultipleChoiceGridSchemaRepository multipleChoiceGridSchemaRepository;

    public MultipleChoiceGridSchemaManager(MultipleChoiceGridSchemaRepository multipleChoiceGridSchemaRepository) {
        this.multipleChoiceGridSchemaRepository = multipleChoiceGridSchemaRepository;
    }

    @Override
    public MultipleChoiceGridResDto get(UUID formId, Long questionId) {
        return toQuestionResDto(
                multipleChoiceGridSchemaRepository.findByFormIdAndId(formId, questionId)
                        .orElseThrow(() -> new QuestionSchemaNotFoundException(questionId))
        );
    }

    @Override
    public boolean validateResponse(MultipleChoiceGridResponseAddReqDto validationDto) {
        var mcg = multipleChoiceGridSchemaRepository.findById(validationDto.getQuestionId())
                .orElseThrow(() -> new QuestionSchemaNotFoundException(QuestionType.MULTIPLE_CHOICE_GRID, validationDto.getQuestionId()));

        if (validationDto.getRows().size() > mcg.getRows().size()) {
            throw new ResponseValidationException(
                    "The response contains more rows than are available. Available rows: "
                            + mcg.getRows().size()
                            + ", received: "
                            + validationDto.getRows().size() + "."
            );
        }

        if (mcg.getEachRowRequired() &&
                (validationDto.getRows().size() != mcg.getRows().size()
                        || validationDto.getRows().stream().anyMatch(r -> r.responseColumnId() == null))) {
            throw new ResponseValidationException(
                    "A response is required for every row. Expected responses for "
                            + mcg.getRows().size()
                            + " rows, but received "
                            + validationDto.getRows().size()
                            + ", or one or more rows have no selected column."
            );
        }

        var rowSet = new HashSet<>(mcg.getRows().stream().map(MultipleChoiceGridRowSchema::getId).toList());
        var columnSet = new HashSet<>(mcg.getColumns().stream().map(MultipleChoiceGridColumnSchema::getId).toList());
        var invalidRows = new ArrayList<Long>();
        var invalidColumns = new HashMap<Long, ArrayList<Long>>();

        validationDto.getRows().forEach(r -> {
            if (!rowSet.contains(r.rowId())) {
                invalidRows.add(r.rowId());
            }
            if (!columnSet.contains(r.responseColumnId())) {
                if (invalidColumns.containsKey(r.rowId())) {
                    invalidColumns.get(r.rowId()).add(r.responseColumnId());
                } else {
                    var list = new ArrayList<Long>();
                    list.add(r.responseColumnId());
                    invalidColumns.put(r.rowId(), list);
                }
            }
        });

        if (!invalidRows.isEmpty()) {
            throw new ResponseValidationException(
                    "The response contains invalid row IDs: " + invalidRows
            );
        }

        if (!invalidColumns.isEmpty()) {
            throw new ResponseValidationException(
                    "The response contains invalid column IDs. Invalid columns by row: " + invalidColumns
            );
        }

        return true;

    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.MULTIPLE_CHOICE_GRID;
    }

    @Override
    public MultipleChoiceGridResDto toQuestionResDto(MultipleChoiceGridSchema questionSchema) {
        var mc = new MultipleChoiceGridResDto();

        populateCommonFields(questionSchema, mc);

        var rows = questionSchema.getRows().stream()
                .map(row ->
                        new MultipleChoiceGridResDto.MultipleChoiceGridRowResDto(row.getId(), row.getRowName(), row.getOrderIndex())
                )
                .toList();
        var columns = questionSchema.getColumns().stream()
                .map(column ->
                        new MultipleChoiceGridResDto.MultipleChoiceGridColumnResDto(column.getId(), column.getColumnName(), column.getOrderIndex())
                )
                .toList();

        mc.setRows(rows);
        mc.setColumns(columns);

        return mc;
    }
}
