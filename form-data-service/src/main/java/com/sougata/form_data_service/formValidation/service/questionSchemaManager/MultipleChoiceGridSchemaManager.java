package com.sougata.form_data_service.formValidation.service.questionSchemaManager;

import com.sougata.form_data_service.constant.QuestionType;
import com.sougata.form_data_service.dto.question.request.MultipleChoiceGridResponseAddReqDto;
import com.sougata.form_data_service.dto.question.response.MultipleChoiceGridResDto;
import com.sougata.form_data_service.formValidation.exception.ResponseValidationException;
import com.sougata.form_data_service.formValidation.service.QuestionSchemaManager;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

@Service("MULTIPLE_CHOICE_GRID_QUESTION_SCHEMA_MANAGER")
public class MultipleChoiceGridSchemaManager extends QuestionSchemaManager<MultipleChoiceGridResDto, MultipleChoiceGridResponseAddReqDto> {

    @Override
    public boolean validateResponse(MultipleChoiceGridResponseAddReqDto validationDto, MultipleChoiceGridResDto mcg) {
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

        var rowSet = new HashSet<>(mcg.getRows().stream().map(MultipleChoiceGridResDto.MultipleChoiceGridRowResDto::id).toList());
        var columnSet = new HashSet<>(mcg.getColumns().stream().map(MultipleChoiceGridResDto.MultipleChoiceGridColumnResDto::id).toList());
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
}
