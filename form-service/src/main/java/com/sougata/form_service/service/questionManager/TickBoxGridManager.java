package com.sougata.form_service.service.questionManager;

import com.sougata.form_service.constant.QuestionType;
import com.sougata.form_service.dto.question.request.TickBoxGridAddUpdateReqDto;
import com.sougata.form_service.dto.question.response.TickBoxGridResDto;
import com.sougata.form_service.exception.QuestionNotFoundException;
import com.sougata.form_service.model.questionSchema.Question;
import com.sougata.form_service.model.questionSchema.TickBoxGrid;
import com.sougata.form_service.model.questionSchema.TickBoxGridColumn;
import com.sougata.form_service.model.questionSchema.TickBoxGridRow;
import com.sougata.form_service.repository.QuestionRepository;
import com.sougata.form_service.repository.TickBoxGridColumnRepository;
import com.sougata.form_service.repository.TickBoxGridRepository;
import com.sougata.form_service.repository.TickBoxGridRowRepository;
import com.sougata.form_service.service.FormService;
import com.sougata.form_service.service.QuestionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service("TICK_BOX_GRID_QUESTION_MANAGER")
public class TickBoxGridManager extends QuestionManager<TickBoxGrid, TickBoxGridAddUpdateReqDto, TickBoxGridResDto> {

    private final TickBoxGridRepository tickBoxGridRepository;
    private final TickBoxGridRowRepository tickBoxGridRowRepository;
    private final TickBoxGridColumnRepository tickBoxGridColumnRepository;

    public TickBoxGridManager(TickBoxGridRepository tickBoxGridRepository, FormService formService, QuestionRepository questionRepository, TickBoxGridRowRepository tickBoxGridRowRepository, TickBoxGridColumnRepository tickBoxGridColumnRepository) {
        super(questionRepository, formService);
        this.tickBoxGridRepository = tickBoxGridRepository;
        this.tickBoxGridRowRepository = tickBoxGridRowRepository;
        this.tickBoxGridColumnRepository = tickBoxGridColumnRepository;
    }

    @Override
    public TickBoxGridResDto get(UUID formId, Long questionId) {
        return toQuestionResDto(tickBoxGridRepository.findByQuestion_FormIdAndQuestion_Id(formId, questionId).orElseThrow(() -> new QuestionNotFoundException(questionId)));
    }

    @Override
    @Transactional
    public TickBoxGridResDto create(UUID formId, TickBoxGridAddUpdateReqDto crudDto) {
        var newTbg = new TickBoxGrid();

        var question = createQuestion(crudDto, formId);

        setPropertiesForNew(crudDto, newTbg, question);

        var savedTbg = tickBoxGridRepository.save(newTbg);

        return toQuestionResDto(savedTbg);
    }

    @Override
    public TickBoxGridResDto create(UUID formId, Long questionId, TickBoxGridAddUpdateReqDto crudDto) {
        var newTbg = new TickBoxGrid();

        var question = updateQuestion(formId, questionId, crudDto);

        setPropertiesForNew(crudDto, newTbg, question);

        var savedTbg = tickBoxGridRepository.save(newTbg);

        return toQuestionResDto(savedTbg);
    }

    @Override
    @Transactional
    public TickBoxGridResDto update(UUID formId, Long questionId, TickBoxGridAddUpdateReqDto crudDto) {
        TickBoxGrid tbg = tickBoxGridRepository.findByQuestion_FormIdAndQuestion_Id(formId, questionId)
                .orElseThrow(() -> new QuestionNotFoundException(QuestionType.TICK_BOX_GRID, questionId));

        updateQuestion(formId, questionId, crudDto);
        tbg.setEachRowRequired(crudDto.getEachRowRequired());

        Map<Long, TickBoxGridRow> existingRows = tbg.getRows().stream()
                .collect(Collectors.toMap(TickBoxGridRow::getId, r -> r));
        Set<Long> requestRowIds = crudDto.getRows().stream()
                .map(TickBoxGridAddUpdateReqDto.Row::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        tbg.getRows().removeIf(row -> !requestRowIds.contains(row.getId()));

        for (int i = 0; i < crudDto.getRows().size(); i++) {
            var dto = crudDto.getRows().get(i);

            if (dto.getId() == null) {
                TickBoxGridRow row = new TickBoxGridRow();
                row.setRowName(dto.getRow());
                row.setOrderIndex(i);
                row.setTickBoxGrid(tbg);

                tbg.getRows().add(row);
            } else {
                TickBoxGridRow row = existingRows.get(dto.getId());

                if (row == null) {
                    throw new IllegalArgumentException("Invalid row id: " + dto.getId());
                }

                row.setRowName(dto.getRow());
                row.setOrderIndex(i);
            }
        }

        Map<Long, TickBoxGridColumn> existingColumns = tbg.getColumns().stream()
                .collect(Collectors.toMap(TickBoxGridColumn::getId, c -> c));
        Set<Long> requestColumnIds = crudDto.getColumns().stream()
                .map(TickBoxGridAddUpdateReqDto.Column::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        tbg.getColumns().removeIf(column -> !requestColumnIds.contains(column.getId()));

        for (int i = 0; i < crudDto.getColumns().size(); i++) {
            var dto = crudDto.getColumns().get(i);

            if (dto.getId() == null) {
                TickBoxGridColumn column = new TickBoxGridColumn();
                column.setColumnName(dto.getColumn());
                column.setOrderIndex(i);
                column.setTickBoxGrid(tbg);

                tbg.getColumns().add(column);
            } else {
                TickBoxGridColumn column = existingColumns.get(dto.getId());

                if (column == null) {
                    throw new IllegalArgumentException("Invalid column id: " + dto.getId());
                }

                column.setColumnName(dto.getColumn());
                column.setOrderIndex(i);
            }
        }

        tickBoxGridRepository.save(tbg);

        return toQuestionResDto(tbg);
    }

    @Override
    public TickBoxGridResDto toQuestionResDto(TickBoxGrid question) {
        var t = new TickBoxGridResDto();

        populateCommonFields(question, t);

        var rows = question.getRows().stream()
                .map(row ->
                        new TickBoxGridResDto.TickBoxGridRowResDto(row.getId(), row.getRowName(), row.getOrderIndex())
                )
                .toList();
        var columns = question.getColumns().stream()
                .map(column ->
                        new TickBoxGridResDto.TickBoxGridColumnResDto(column.getId(), column.getColumnName(), column.getOrderIndex())
                )
                .toList();

        t.setEachRowRequired(question.getEachRowRequired());
        t.setRows(rows);
        t.setColumns(columns);

        return t;
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.TICK_BOX_GRID;
    }

    @Override
    @Transactional
    public void delete(UUID formId, Long questionId) {
        tickBoxGridRowRepository.deleteAllByFormIdAndTickBoxGridId(formId, questionId);
        tickBoxGridColumnRepository.deleteAllByFormIdAndTickBoxGridId(formId, questionId);
        tickBoxGridRepository.deleteQuestion(formId, questionId);
    }

    private void setPropertiesForNew(TickBoxGridAddUpdateReqDto source, TickBoxGrid target, Question question) {
        var rows = new ArrayList<TickBoxGridRow>();
        var columns = new ArrayList<TickBoxGridColumn>();

        for (int i = 0; i < source.getRows().size(); i++) {
            var row = source.getRows().get(i);
            var tbgRow = new TickBoxGridRow();

            tbgRow.setRowName(row.getRow());
            tbgRow.setTickBoxGrid(target);
            tbgRow.setOrderIndex(i);

            rows.add(tbgRow);
        }

        for (int i = 0; i < source.getColumns().size(); i++) {
            var column = source.getColumns().get(i);
            var tbgColumn = new TickBoxGridColumn();

            tbgColumn.setColumnName(column.getColumn());
            tbgColumn.setTickBoxGrid(target);
            tbgColumn.setOrderIndex(i);

            columns.add(tbgColumn);
        }

        target.setRows(rows);
        target.setColumns(columns);
        target.setEachRowRequired(source.getEachRowRequired());
        target.setQuestion(question);
    }
}
