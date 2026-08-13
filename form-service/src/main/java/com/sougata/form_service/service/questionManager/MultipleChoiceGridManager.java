package com.sougata.form_service.service.questionManager;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.question.request.MultipleChoiceGridAddUpdateReqDto;
import com.sougata.form_service.dto.question.response.MultipleChoiceGridResDto;
import com.sougata.form_service.exception.QuestionNotFoundException;
import com.sougata.form_service.model.MultipleChoiceGrid;
import com.sougata.form_service.model.MultipleChoiceGridColumn;
import com.sougata.form_service.model.MultipleChoiceGridRow;
import com.sougata.form_service.model.Question;
import com.sougata.form_service.repository.MultipleChoiceGridColumnRepository;
import com.sougata.form_service.repository.MultipleChoiceGridRepository;
import com.sougata.form_service.repository.MultipleChoiceGridRowRepository;
import com.sougata.form_service.repository.QuestionRepository;
import com.sougata.form_service.service.FormService;
import com.sougata.form_service.service.QuestionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service("MULTIPLE_CHOICE_GRID_QUESTION_MANAGER")
public class MultipleChoiceGridManager extends QuestionManager<MultipleChoiceGrid, MultipleChoiceGridAddUpdateReqDto, MultipleChoiceGridResDto> {

    private final MultipleChoiceGridRepository multipleChoiceGridRepository;
    private final MultipleChoiceGridRowRepository multipleChoiceGridRowRepository;
    private final MultipleChoiceGridColumnRepository multipleChoiceGridColumnRepository;

    public MultipleChoiceGridManager(MultipleChoiceGridRepository multipleChoiceGridRepository, FormService formService, QuestionRepository questionRepository, MultipleChoiceGridRowRepository multipleChoiceGridRowRepository, MultipleChoiceGridColumnRepository multipleChoiceGridColumnRepository) {
        super(questionRepository, formService);
        this.multipleChoiceGridRepository = multipleChoiceGridRepository;
        this.multipleChoiceGridRowRepository = multipleChoiceGridRowRepository;
        this.multipleChoiceGridColumnRepository = multipleChoiceGridColumnRepository;
    }

    @Override
    public MultipleChoiceGridResDto get(UUID formId, Long questionId) {
        return toQuestionResDto(multipleChoiceGridRepository.findByQuestion_FormIdAndQuestion_Id(formId, questionId).orElseThrow(() -> new QuestionNotFoundException(questionId)));
    }

    @Override
    @Transactional(transactionManager = "schemaTransactionManager")
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

        var question = updateQuestion(formId, questionId, crudDto);

        setPropertiesForNew(crudDto, newMcg, question);

        var saved = multipleChoiceGridRepository.save(newMcg);

        return toQuestionResDto(saved);
    }

    @Override
    @Transactional(transactionManager = "schemaTransactionManager")
    public MultipleChoiceGridResDto update(UUID formId, Long questionId, MultipleChoiceGridAddUpdateReqDto crudDto) {
        MultipleChoiceGrid mcg = multipleChoiceGridRepository.findByQuestion_FormIdAndQuestion_Id(formId, questionId)
                .orElseThrow(() -> new QuestionNotFoundException(QuestionType.MULTIPLE_CHOICE_GRID, questionId));

        updateQuestion(formId, questionId, crudDto);
        mcg.setEachRowRequired(crudDto.getEachRowRequired());

        Map<Long, MultipleChoiceGridRow> existingRows = mcg.getRows().stream()
                .collect(Collectors.toMap(MultipleChoiceGridRow::getId, row -> row));
        Set<Long> requestRowIds = crudDto.getRows().stream()
                .map(MultipleChoiceGridAddUpdateReqDto.Row::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        mcg.getRows().removeIf(row -> !requestRowIds.contains(row.getId()));

        for (int i = 0; i < crudDto.getRows().size(); i++) {
            var dto = crudDto.getRows().get(i);

            if (dto.getId() == null) {
                MultipleChoiceGridRow row = new MultipleChoiceGridRow();
                row.setRowName(dto.getRow());
                row.setOrderIndex(i);
                row.setMultipleChoiceGrid(mcg);

                mcg.getRows().add(row);
            } else {
                MultipleChoiceGridRow row = existingRows.get(dto.getId());

                if (row == null) {
                    throw new IllegalArgumentException("Invalid row id: " + dto.getId());
                }

                row.setRowName(dto.getRow());
                row.setOrderIndex(i);
            }
        }

        Map<Long, MultipleChoiceGridColumn> existingColumns = mcg.getColumns().stream()
                .collect(Collectors.toMap(MultipleChoiceGridColumn::getId, column -> column));
        Set<Long> requestColumnIds = crudDto.getColumns().stream()
                .map(MultipleChoiceGridAddUpdateReqDto.Column::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        mcg.getColumns().removeIf(column -> !requestColumnIds.contains(column.getId()));

        for (int i = 0; i < crudDto.getColumns().size(); i++) {
            var dto = crudDto.getColumns().get(i);

            if (dto.getId() == null) {
                MultipleChoiceGridColumn column = new MultipleChoiceGridColumn();
                column.setColumnName(dto.getColumn());
                column.setOrderIndex(i);
                column.setMultipleChoiceGrid(mcg);

                mcg.getColumns().add(column);
            } else {
                MultipleChoiceGridColumn column = existingColumns.get(dto.getId());

                if (column == null) {
                    throw new IllegalArgumentException("Invalid column id: " + dto.getId());
                }

                column.setColumnName(dto.getColumn());
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
                .sorted(Comparator.comparingInt(MultipleChoiceGridResDto.MultipleChoiceGridRowResDto::getOrderIndex))
                .toList();

        var columns = question.getColumns().stream()
                .map(column ->
                        new MultipleChoiceGridResDto.MultipleChoiceGridColumnResDto(column.getId(), column.getColumnName(), column.getOrderIndex())
                )
                .sorted(Comparator.comparingInt(MultipleChoiceGridResDto.MultipleChoiceGridColumnResDto::getOrderIndex))
                .toList();

        mc.setEachRowRequired(question.getEachRowRequired());
        mc.setRows(rows);
        mc.setColumns(columns);

        return mc;
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.MULTIPLE_CHOICE_GRID;
    }

    @Override
    @Transactional(transactionManager = "schemaTransactionManager")
    public void delete(UUID formId, Long questionId) {
        multipleChoiceGridRepository.deleteQuestion(formId, questionId);
    }

    private void setPropertiesForNew(MultipleChoiceGridAddUpdateReqDto source, MultipleChoiceGrid target, Question question) {
        var rows = new ArrayList<MultipleChoiceGridRow>();
        var columns = new ArrayList<MultipleChoiceGridColumn>();

        for (int i = 0; i < source.getRows().size(); i++) {
            var row = source.getRows().get(i);
            var mcgRow = new MultipleChoiceGridRow();

            mcgRow.setRowName(row.getRow());
            mcgRow.setMultipleChoiceGrid(target);
            mcgRow.setOrderIndex(i);

            rows.add(mcgRow);
        }

        for (int i = 0; i < source.getColumns().size(); i++) {
            var column = source.getColumns().get(i);
            var mcgColumn = new MultipleChoiceGridColumn();

            mcgColumn.setColumnName(column.getColumn());
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
