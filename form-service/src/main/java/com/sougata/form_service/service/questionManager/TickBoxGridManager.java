package com.sougata.form_service.service.questionManager;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.question.request.TickBoxGridAddUpdateReqDto;
import com.sougata.form_service.dto.question.response.TickBoxGridResDto;
import com.sougata.form_service.dto.validation.request.TickBoxGridValidationRequestDto;
import com.sougata.form_service.exception.QuestionNotFoundException;
import com.sougata.form_service.exception.ResponseValidationException;
import com.sougata.form_service.model.questionSchema.TickBoxGrid;
import com.sougata.form_service.model.questionSchema.TickBoxGridColumn;
import com.sougata.form_service.model.questionSchema.TickBoxGridRow;
import com.sougata.form_service.repository.TickBoxGridRepository;
import com.sougata.form_service.service.FormService;
import com.sougata.form_service.service.QuestionManager;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service("TICK_BOX_GRID_QUESTION_MANAGER")
public class TickBoxGridManager extends QuestionManager<TickBoxGridAddUpdateReqDto, TickBoxGridResDto, TickBoxGridValidationRequestDto> {

    private final TickBoxGridRepository tickBoxGridRepository;
    private final FormService formService;

    public TickBoxGridManager(TickBoxGridRepository tickBoxGridRepository, FormService formService) {
        this.tickBoxGridRepository = tickBoxGridRepository;
        this.formService = formService;
    }

    @Override
    public TickBoxGridResDto get(UUID formId, Long questionId) {
        return TickBoxGridResDto.create(tickBoxGridRepository.findByFormIdAndId(formId, questionId).orElseThrow(() -> new QuestionNotFoundException(questionId)));
    }

    @Override
    public TickBoxGridResDto create(UUID formId, TickBoxGridAddUpdateReqDto crudDto) {
        TickBoxGrid newTbg = new TickBoxGrid();

        setProperties(crudDto, formId, newTbg);

        TickBoxGrid saved = tickBoxGridRepository.save(newTbg);

        return TickBoxGridResDto.create(saved);
    }

    @Override
    public TickBoxGridResDto create(UUID formId, Long questionId, TickBoxGridAddUpdateReqDto crudDto) {
        TickBoxGrid newTbg = new TickBoxGrid();

        newTbg.setId(questionId);
        setProperties(crudDto, formId, newTbg);

        TickBoxGrid saved = tickBoxGridRepository.save(newTbg);

        return TickBoxGridResDto.create(saved);
    }

    @Override
    public TickBoxGridResDto update(Long questionId, TickBoxGridAddUpdateReqDto crudDto) {
        TickBoxGrid tbg = tickBoxGridRepository.findById(questionId)
                .orElseThrow(() -> new QuestionNotFoundException(QuestionType.TICK_BOX_GRID, questionId));

        setProperties(crudDto, tbg);

        tickBoxGridRepository.save(tbg);

        return TickBoxGridResDto.create(tbg);
    }

    @Override
    public boolean exists(Long questionId) {
        return tickBoxGridRepository.existsById(questionId);
    }

    @Override
    public void delete(Long questionId) {
        tickBoxGridRepository.deleteById(questionId);
    }

    @Override
    public boolean validateResponse(TickBoxGridValidationRequestDto validationDto) {
        var tbg = tickBoxGridRepository.findById(validationDto.getQuestionId())
                .orElseThrow(() -> new QuestionNotFoundException(QuestionType.TICK_BOX_GRID, validationDto.getQuestionId()));

        if (validationDto.getRows().size() > tbg.getRows().size()) {
            throw new ResponseValidationException(
                    "The number of response rows cannot exceed the number of available rows. Available rows: "
                            + tbg.getRows().size()
                            + ", received: "
                            + validationDto.getRows().size()
            );
        }

        var validRowIds = new HashSet<>(tbg.getRows().stream()
                .map(TickBoxGridRow::getId)
                .toList());

        var validColumnIds = new HashSet<>(tbg.getColumns().stream()
                .map(TickBoxGridColumn::getId)
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
                    .map(TickBoxGridValidationRequestDto.Row::rowId)
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
    public Class<TickBoxGridAddUpdateReqDto> getCrudDtoClass() {
        return TickBoxGridAddUpdateReqDto.class;
    }

    @Override
    public Class<TickBoxGridValidationRequestDto> getValidationDtoClass() {
        return TickBoxGridValidationRequestDto.class;
    }

    @Override
    @SuppressWarnings("unchecked")
    public TickBoxGridRepository getQuestionRepository() {
        return tickBoxGridRepository;
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.TICK_BOX_GRID;
    }

    private void setProperties(TickBoxGridAddUpdateReqDto source, UUID formId, TickBoxGrid target) {

        target.setQuestion(source.getQuestion());
        target.setDescription(source.getDescription());
        target.setRequired(source.getRequired());
        target.setEachRowRequired(source.getEachRowRequired());
        target.setOrderIndex(source.getOrderIndex());

        Map<Long, TickBoxGridRow> existingRows = target.getRows().stream()
                .collect(Collectors.toMap(TickBoxGridRow::getId, r -> r));

        Set<Long> requestRowIds = source.getRows().stream()
                .map(TickBoxGridAddUpdateReqDto.Row::id)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        target.getRows().removeIf(row -> !requestRowIds.contains(row.getId()));

        for (int i = 0; i < source.getRows().size(); i++) {

            var dto = source.getRows().get(i);

            if (dto.id() == null) {

                TickBoxGridRow row = new TickBoxGridRow();
                row.setRowName(dto.row());
                row.setOrderIndex(i);
                row.setTickBoxGrid(target);

                target.getRows().add(row);

            } else {
                TickBoxGridRow row = existingRows.get(dto.id());

                if (row == null) {
                    throw new IllegalArgumentException("Invalid row id: " + dto.id());
                }

                row.setRowName(dto.row());
                row.setOrderIndex(i);
            }
        }

        Map<Long, TickBoxGridColumn> existingColumns = target.getColumns().stream()
                .collect(Collectors.toMap(TickBoxGridColumn::getId, c -> c));

        Set<Long> requestColumnIds = source.getColumns().stream()
                .map(TickBoxGridAddUpdateReqDto.Column::id)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        target.getColumns().removeIf(column -> !requestColumnIds.contains(column.getId()));

        for (int i = 0; i < source.getColumns().size(); i++) {

            var dto = source.getColumns().get(i);

            if (dto.id() == null) {

                TickBoxGridColumn column = new TickBoxGridColumn();
                column.setColumnName(dto.column());
                column.setOrderIndex(i);
                column.setTickBoxGrid(target);

                target.getColumns().add(column);

            } else {

                TickBoxGridColumn column = existingColumns.get(dto.id());

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

    private void setProperties(TickBoxGridAddUpdateReqDto source, TickBoxGrid target) {
        setProperties(source, null, target);
    }
}
