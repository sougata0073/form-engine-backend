package com.sougata.form_data_service.form_schema.service.questionSchemaManager;

import com.sougata.form_data_service.constant.QuestionType;
import com.sougata.form_data_service.dto.question.request.TickBoxGridResponseAddReqDto;
import com.sougata.form_data_service.dto.question.response.TickBoxGridResDto;
import com.sougata.form_data_service.form_schema.exception.ResponseValidationException;
import com.sougata.form_data_service.form_schema.service.QuestionSchemaManager;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

@Service("TICK_BOX_GRID_QUESTION_SCHEMA_MANAGER")
public class TickBoxGridSchemaManager extends QuestionSchemaManager<TickBoxGridResDto, TickBoxGridResponseAddReqDto> {

    @Override
    public boolean validateResponse(TickBoxGridResponseAddReqDto validationDto, TickBoxGridResDto tbg) {
        if (validationDto.getRows().size() > tbg.getRows().size()) {
            throw new ResponseValidationException(
                    "The number of response rows cannot exceed the number of available rows. Available rows: "
                            + tbg.getRows().size()
                            + ", received: "
                            + validationDto.getRows().size()
            );
        }

        var validRowIds = new HashSet<>(tbg.getRows().stream()
                .map(TickBoxGridResDto.TickBoxGridRowResDto::id)
                .toList());

        var validColumnIds = new HashSet<>(tbg.getColumns().stream()
                .map(TickBoxGridResDto.TickBoxGridColumnResDto::id)
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

}
