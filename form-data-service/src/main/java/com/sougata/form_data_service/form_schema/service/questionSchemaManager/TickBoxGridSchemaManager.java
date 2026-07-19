package com.sougata.form_data_service.form_schema.service.questionSchemaManager;

import com.sougata.form_data_service.constant.QuestionType;
import com.sougata.form_data_service.dto.question.request.TickBoxGridResponseAddReqDto;
import com.sougata.form_data_service.form_schema.dto.questionSchema.response.TickBoxGridResDto;
import com.sougata.form_data_service.form_schema.exception.QuestionSchemaNotFoundException;
import com.sougata.form_data_service.form_schema.exception.ResponseValidationException;
import com.sougata.form_data_service.form_schema.model.TickBoxGridColumnSchema;
import com.sougata.form_data_service.form_schema.model.TickBoxGridRowSchema;
import com.sougata.form_data_service.form_schema.model.TickBoxGridSchema;
import com.sougata.form_data_service.form_schema.repository.TickBoxGridSchemaRepository;
import com.sougata.form_data_service.form_schema.service.QuestionSchemaManager;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

@Service("TICK_BOX_GRID_QUESTION_SCHEMA_MANAGER")
public class TickBoxGridSchemaManager extends QuestionSchemaManager<TickBoxGridSchema, TickBoxGridResDto, TickBoxGridResponseAddReqDto> {

    private final TickBoxGridSchemaRepository tickBoxGridSchemaRepository;

    public TickBoxGridSchemaManager(TickBoxGridSchemaRepository tickBoxGridSchemaRepository) {
        this.tickBoxGridSchemaRepository = tickBoxGridSchemaRepository;
    }

    @Override
    public TickBoxGridResDto get(UUID formId, Long questionId) {
        return toQuestionResDto(
                tickBoxGridSchemaRepository.findByFormIdAndId(formId, questionId)
                        .orElseThrow(() -> new QuestionSchemaNotFoundException(questionId))
        );
    }

    @Override
    public boolean validateResponse(TickBoxGridResponseAddReqDto validationDto) {
        var tbg = tickBoxGridSchemaRepository.findById(validationDto.getQuestionId())
                .orElseThrow(() -> new QuestionSchemaNotFoundException(QuestionType.TICK_BOX_GRID, validationDto.getQuestionId()));

        if (validationDto.getRows().size() > tbg.getRows().size()) {
            throw new ResponseValidationException(
                    "The number of response rows cannot exceed the number of available rows. Available rows: "
                            + tbg.getRows().size()
                            + ", received: "
                            + validationDto.getRows().size()
            );
        }

        var validRowIds = new HashSet<>(tbg.getRows().stream()
                .map(TickBoxGridRowSchema::getId)
                .toList());

        var validColumnIds = new HashSet<>(tbg.getColumns().stream()
                .map(TickBoxGridColumnSchema::getId)
                .toList());

        var invalidRowIds = new ArrayList<Long>();
        var invalidColumnIds = new HashMap<Long, ArrayList<Long>>();

        validationDto.getRows().forEach(r -> {
            if (!validRowIds.contains(r.rowId())) {
                invalidRowIds.add(r.rowId());
                return;
            }

            r.responseColumnIds().forEach(columnId -> {
                if (columnId == null || !validColumnIds.contains(columnId)) {
                    invalidColumnIds
                            .computeIfAbsent(r.rowId(), k -> new ArrayList<>())
                            .add(columnId);
                }
            });
        });

        if (!invalidRowIds.isEmpty()) {
            throw new ResponseValidationException(
                    "The following row IDs are not valid: " + invalidRowIds
            );
        }

        if (!invalidColumnIds.isEmpty()) {
            throw new ResponseValidationException(
                    "The response contains invalid column IDs. Invalid columns by row: " + invalidColumnIds
            );
        }

        if (tbg.getEachRowRequired()) {

            if (validationDto.getRows().size() != tbg.getRows().size()) {
                throw new ResponseValidationException(
                        "A response is required for every row. Expected responses for "
                                + tbg.getRows().size()
                                + " rows, but received "
                                + validationDto.getRows().size() + "."
                );
            }

            var rowsWithoutSelection = validationDto.getRows().stream()
                    .filter(r -> r.responseColumnIds().isEmpty())
                    .map(TickBoxGridResponseAddReqDto.Row::rowId)
                    .toList();

            if (!rowsWithoutSelection.isEmpty()) {
                throw new ResponseValidationException(
                        "The following row IDs must contain at least one selected column: "
                                + rowsWithoutSelection
                );
            }
        }

        return true;
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.TICK_BOX_GRID;
    }

    @Override
    public TickBoxGridResDto toQuestionResDto(TickBoxGridSchema questionSchema) {
        var t = new TickBoxGridResDto();

        populateCommonFields(questionSchema, t);

        var rows = questionSchema.getRows().stream()
                .map(row ->
                        new TickBoxGridResDto.TickBoxGridRowResDto(row.getId(), row.getRowName(), row.getOrderIndex())
                )
                .toList();
        var columns = questionSchema.getColumns().stream()
                .map(column ->
                        new TickBoxGridResDto.TickBoxGridColumnResDto(column.getId(), column.getColumnName(), column.getOrderIndex())
                )
                .toList();

        t.setRows(rows);
        t.setColumns(columns);

        return t;
    }

}
