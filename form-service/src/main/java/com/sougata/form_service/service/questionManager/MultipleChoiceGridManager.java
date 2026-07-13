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
import com.sougata.form_service.repository.MultipleChoiceGridRepository;
import com.sougata.form_service.service.FormService;
import com.sougata.form_service.service.QuestionManager;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service("MULTIPLE_CHOICE_GRID_QUESTION_MANAGER")
public class MultipleChoiceGridManager extends QuestionManager<MultipleChoiceGridAddUpdateReqDto, MultipleChoiceGridResDto, MultipleChoiceGridValidationRequestDto> {

    private final MultipleChoiceGridRepository multipleChoiceGridRepository;
    private final FormService formService;

    public MultipleChoiceGridManager(MultipleChoiceGridRepository multipleChoiceGridRepository, FormService formService) {
        this.multipleChoiceGridRepository = multipleChoiceGridRepository;
        this.formService = formService;
    }

    @Override
    public MultipleChoiceGridResDto get(UUID formId, Long questionId) {
        return MultipleChoiceGridResDto.create(multipleChoiceGridRepository.findByFormIdAndId(formId, questionId).orElseThrow(() -> new QuestionNotFoundException(questionId)));
    }

    @Override
    public MultipleChoiceGridResDto create(UUID formId, MultipleChoiceGridAddUpdateReqDto crudDto) {
        MultipleChoiceGrid newMcg = new MultipleChoiceGrid();

        setProperties(crudDto, formId, newMcg);

        MultipleChoiceGrid saved = multipleChoiceGridRepository.save(newMcg);

        return MultipleChoiceGridResDto.create(saved);
    }

    @Override
    public MultipleChoiceGridResDto create(UUID formId, Long questionId, MultipleChoiceGridAddUpdateReqDto crudDto) {
        MultipleChoiceGrid newMcg = new MultipleChoiceGrid();

        newMcg.setId(questionId);
        setProperties(crudDto, formId, newMcg);

        MultipleChoiceGrid saved = multipleChoiceGridRepository.save(newMcg);

        return MultipleChoiceGridResDto.create(saved);
    }

    @Override
    public MultipleChoiceGridResDto update(Long questionId, MultipleChoiceGridAddUpdateReqDto crudDto) {
        MultipleChoiceGrid mcg = multipleChoiceGridRepository.findById(questionId)
                .orElseThrow(() -> new QuestionNotFoundException(QuestionType.MULTIPLE_CHOICE_GRID, questionId));
        setProperties(crudDto, mcg);
        multipleChoiceGridRepository.save(mcg);

        return MultipleChoiceGridResDto.create(mcg);
    }

    @Override
    public boolean exists(Long questionId) {
        return multipleChoiceGridRepository.existsById(questionId);
    }

    @Override
    public void delete(Long questionId) {
        multipleChoiceGridRepository.deleteById(questionId);
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
    public Class<MultipleChoiceGridAddUpdateReqDto> getCrudDtoClass() {
        return MultipleChoiceGridAddUpdateReqDto.class;
    }

    @Override
    public Class<MultipleChoiceGridValidationRequestDto> getValidationDtoClass() {
        return MultipleChoiceGridValidationRequestDto.class;
    }

    @Override
    @SuppressWarnings("unchecked")
    public MultipleChoiceGridRepository getQuestionRepository() {
        return multipleChoiceGridRepository;
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.MULTIPLE_CHOICE_GRID;
    }

    private void setProperties(MultipleChoiceGridAddUpdateReqDto source, UUID formId, MultipleChoiceGrid target) {

        target.setQuestion(source.getQuestion());
        target.setDescription(source.getDescription());
        target.setRequired(source.getRequired());
        target.setEachRowRequired(source.getEachRowRequired());
        target.setOrderIndex(source.getOrderIndex());

        Map<Long, MultipleChoiceGridRow> existingRows = target.getRows().stream()
                .collect(Collectors.toMap(MultipleChoiceGridRow::getId, row -> row));

        Set<Long> requestRowIds = source.getRows().stream()
                .map(MultipleChoiceGridAddUpdateReqDto.Row::id)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        target.getRows().removeIf(row -> !requestRowIds.contains(row.getId()));

        for (int i = 0; i < source.getRows().size(); i++) {

            var dto = source.getRows().get(i);

            if (dto.id() == null) {

                MultipleChoiceGridRow row = new MultipleChoiceGridRow();
                row.setRowName(dto.row());
                row.setOrderIndex(i);
                row.setMultipleChoiceGrid(target);

                target.getRows().add(row);

            } else {

                MultipleChoiceGridRow row = existingRows.get(dto.id());

                if (row == null) {
                    throw new IllegalArgumentException("Invalid row id: " + dto.id());
                }

                row.setRowName(dto.row());
                row.setOrderIndex(i);
            }
        }

        Map<Long, MultipleChoiceGridColumn> existingColumns = target.getColumns().stream()
                .collect(Collectors.toMap(MultipleChoiceGridColumn::getId, column -> column));

        Set<Long> requestColumnIds = source.getColumns().stream()
                .map(MultipleChoiceGridAddUpdateReqDto.Column::id)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        target.getColumns().removeIf(column -> !requestColumnIds.contains(column.getId()));

        for (int i = 0; i < source.getColumns().size(); i++) {

            var dto = source.getColumns().get(i);

            if (dto.id() == null) {

                MultipleChoiceGridColumn column = new MultipleChoiceGridColumn();
                column.setColumnName(dto.column());
                column.setOrderIndex(i);
                column.setMultipleChoiceGrid(target);

                target.getColumns().add(column);

            } else {

                MultipleChoiceGridColumn column = existingColumns.get(dto.id());

                if (column == null) {
                    throw new IllegalArgumentException("Invalid column id: " + dto.id());
                }

                column.setColumnName(dto.column());
                column.setOrderIndex(i);
            }
        }

        if (formId != null) {
            target.setForm(formService.getFormById(formId));
        }
    }

    private void setProperties(MultipleChoiceGridAddUpdateReqDto source, MultipleChoiceGrid target) {
        setProperties(source, null, target);
    }
}
