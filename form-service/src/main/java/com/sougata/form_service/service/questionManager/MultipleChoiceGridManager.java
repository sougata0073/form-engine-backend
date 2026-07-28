package com.sougata.form_service.service.questionManager;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.question.request.MultipleChoiceGridAddUpdateReqDto;
import com.sougata.form_service.dto.question.response.MultipleChoiceGridResDto;
import com.sougata.form_service.dto.validation.request.MultipleChoiceGridValidationRequestDto;
import com.sougata.form_service.exception.QuestionNotFoundException;
import com.sougata.form_service.exception.ResponseValidationException;
import com.sougata.form_service.model.questionSchema.MultipleChoiceGrid;
import com.sougata.form_service.model.questionSchema.MultipleChoiceGridColumn;
import com.sougata.form_service.model.questionSchema.MultipleChoiceGridRow;
import com.sougata.form_service.model.questionSchema.Question;
import com.sougata.form_service.repository.MultipleChoiceGridRepository;
import com.sougata.form_service.repository.QuestionRepository;
import com.sougata.form_service.service.FormService;
import com.sougata.form_service.service.QuestionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service("MULTIPLE_CHOICE_GRID_QUESTION_MANAGER")
public class MultipleChoiceGridManager extends QuestionManager<MultipleChoiceGrid, MultipleChoiceGridAddUpdateReqDto, MultipleChoiceGridResDto, MultipleChoiceGridValidationRequestDto> {

    private final MultipleChoiceGridRepository multipleChoiceGridRepository;

    public MultipleChoiceGridManager(MultipleChoiceGridRepository multipleChoiceGridRepository, FormService formService, QuestionRepository questionRepository) {
        super(questionRepository, formService);
        this.multipleChoiceGridRepository = multipleChoiceGridRepository;
    }

    @Override
    public MultipleChoiceGridResDto get(UUID formId, Long questionId) {
        return toQuestionResDto(multipleChoiceGridRepository.findByQuestion_FormIdAndQuestion_Id(formId, questionId).orElseThrow(() -> new QuestionNotFoundException(questionId)));
    }

    @Override
    @Transactional
    public MultipleChoiceGridResDto create(UUID formId, MultipleChoiceGridAddUpdateReqDto crudDto) {
        var newMcg = new MultipleChoiceGrid();

        var question = createQuestion(crudDto, formId);

        setPropertiesForNew(crudDto, newMcg, question);

        var saved = multipleChoiceGridRepository.save(newMcg);

        return toQuestionResDto(saved);
    }

    @Override
    public MultipleChoiceGridResDto create(UUID formId, Long questionId, MultipleChoiceGridAddUpdateReqDto crudDto) {
        var newMcg = new MultipleChoiceGrid();

        var question = updateQuestion(questionId, crudDto);

        setPropertiesForNew(crudDto, newMcg, question);

        var saved = multipleChoiceGridRepository.save(newMcg);

        return toQuestionResDto(saved);
    }

    @Override
    @Transactional
    public MultipleChoiceGridResDto update(Long questionId, MultipleChoiceGridAddUpdateReqDto crudDto) {
        MultipleChoiceGrid mcg = multipleChoiceGridRepository.findById(questionId)
                .orElseThrow(() -> new QuestionNotFoundException(QuestionType.MULTIPLE_CHOICE_GRID, questionId));

        updateQuestion(questionId, crudDto);
        mcg.setEachRowRequired(crudDto.getEachRowRequired());

        Map<Long, MultipleChoiceGridRow> existingRows = mcg.getRows().stream()
                .collect(Collectors.toMap(MultipleChoiceGridRow::getId, row -> row));
        Set<Long> requestRowIds = crudDto.getRows().stream()
                .map(MultipleChoiceGridAddUpdateReqDto.Row::id)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        mcg.getRows().removeIf(row -> !requestRowIds.contains(row.getId()));

        for (int i = 0; i < crudDto.getRows().size(); i++) {
            var dto = crudDto.getRows().get(i);

            if (dto.id() == null) {
                MultipleChoiceGridRow row = new MultipleChoiceGridRow();
                row.setRowName(dto.row());
                row.setOrderIndex(i);
                row.setMultipleChoiceGrid(mcg);

                mcg.getRows().add(row);
            } else {
                MultipleChoiceGridRow row = existingRows.get(dto.id());

                if (row == null) {
                    throw new IllegalArgumentException("Invalid row id: " + dto.id());
                }

                row.setRowName(dto.row());
                row.setOrderIndex(i);
            }
        }

        Map<Long, MultipleChoiceGridColumn> existingColumns = mcg.getColumns().stream()
                .collect(Collectors.toMap(MultipleChoiceGridColumn::getId, column -> column));
        Set<Long> requestColumnIds = crudDto.getColumns().stream()
                .map(MultipleChoiceGridAddUpdateReqDto.Column::id)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        mcg.getColumns().removeIf(column -> !requestColumnIds.contains(column.getId()));

        for (int i = 0; i < crudDto.getColumns().size(); i++) {
            var dto = crudDto.getColumns().get(i);

            if (dto.id() == null) {
                MultipleChoiceGridColumn column = new MultipleChoiceGridColumn();
                column.setColumnName(dto.column());
                column.setOrderIndex(i);
                column.setMultipleChoiceGrid(mcg);

                mcg.getColumns().add(column);
            } else {
                MultipleChoiceGridColumn column = existingColumns.get(dto.id());

                if (column == null) {
                    throw new IllegalArgumentException("Invalid column id: " + dto.id());
                }

                column.setColumnName(dto.column());
                column.setOrderIndex(i);
            }
        }

        multipleChoiceGridRepository.save(mcg);

        return toQuestionResDto(mcg);
    }

    @Override
    public MultipleChoiceGridResDto toQuestionResDto(MultipleChoiceGrid question) {
        var mc = new MultipleChoiceGridResDto();

        populateCommonFields(question, mc);

        var rows = question.getRows().stream()
                .map(row ->
                        new MultipleChoiceGridResDto.MultipleChoiceGridRowResDto(row.getId(), row.getRowName(), row.getOrderIndex())
                )
                .toList();
        var columns = question.getColumns().stream()
                .map(column ->
                        new MultipleChoiceGridResDto.MultipleChoiceGridColumnResDto(column.getId(), column.getColumnName(), column.getOrderIndex())
                )
                .toList();

        mc.setEachRowRequired(question.getEachRowRequired());
        mc.setRows(rows);
        mc.setColumns(columns);

        return mc;
    }

    @Override
    public boolean validateResponse(MultipleChoiceGridValidationRequestDto validationDto) {
        var mcg = multipleChoiceGridRepository.findById(validationDto.getQuestionId())
                .orElseThrow(() -> new QuestionNotFoundException(QuestionType.MULTIPLE_CHOICE_GRID, validationDto.getQuestionId()));

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

        var rowSet = new HashSet<>(mcg.getRows().stream().map(MultipleChoiceGridRow::getId).toList());
        var columnSet = new HashSet<>(mcg.getColumns().stream().map(MultipleChoiceGridColumn::getId).toList());
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
    public void delete(Long questionId) {
        multipleChoiceGridRepository.deleteById(questionId);
    }

    private void setPropertiesForNew(MultipleChoiceGridAddUpdateReqDto source, MultipleChoiceGrid target, Question question) {
        var rows = new ArrayList<MultipleChoiceGridRow>();
        var columns = new ArrayList<MultipleChoiceGridColumn>();

        for (int i = 0; i < source.getRows().size(); i++) {
            var row = source.getRows().get(i);
            var mcgRow = new MultipleChoiceGridRow();

            mcgRow.setRowName(row.row());
            mcgRow.setMultipleChoiceGrid(target);
            mcgRow.setOrderIndex(i);

            rows.add(mcgRow);
        }

        for (int i = 0; i < source.getColumns().size(); i++) {
            var column = source.getColumns().get(i);
            var mcgColumn = new MultipleChoiceGridColumn();

            mcgColumn.setColumnName(column.column());
            mcgColumn.setMultipleChoiceGrid(target);
            mcgColumn.setOrderIndex(i);

            columns.add(mcgColumn);
        }

        target.setRows(rows);
        target.setColumns(columns);
        target.setEachRowRequired(source.getEachRowRequired());
        target.setQuestion(question);
    }
}
