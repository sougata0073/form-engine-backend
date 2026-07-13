package com.sougata.form_service.model.templateSchema;

import com.sougata.form_service.model.Form;
import com.sougata.form_service.model.questionSchema.MultipleChoiceGrid;
import com.sougata.form_service.model.questionSchema.MultipleChoiceGridColumn;
import com.sougata.form_service.model.questionSchema.MultipleChoiceGridRow;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "multiple_choice_grid_templates")
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MultipleChoiceGridTemplate extends QuestionTemplate<MultipleChoiceGridTemplate> {

    @Column(nullable = false)
    private Boolean eachRowRequired;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "multipleChoiceGridTemplate")
    private List<MultipleChoiceGridRowTemplate> rows = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "multipleChoiceGridTemplate")
    private List<MultipleChoiceGridColumnTemplate> columns = new ArrayList<>();

    @Override
    public MultipleChoiceGrid fromTemplate(Form f, MultipleChoiceGridTemplate t) {
        var multipleChoiceGrid = new MultipleChoiceGrid();

        multipleChoiceGrid.setQuestion(t.getQuestion());
        multipleChoiceGrid.setDescription(t.getDescription());
        multipleChoiceGrid.setRequired(t.getRequired());
        multipleChoiceGrid.setForm(f);
        multipleChoiceGrid.setOrderIndex(t.getOrderIndex());
        multipleChoiceGrid.setEachRowRequired(t.getEachRowRequired());

        var rows = t.getRows()
                .stream()
                .map(op -> {
                    var row = new MultipleChoiceGridRow();

                    row.setRowName(op.getRowName());
                    row.setOrderIndex(op.getOrderIndex());
                    row.setMultipleChoiceGrid(multipleChoiceGrid);

                    return row;
                })
                .toList();

        var columns = t.getColumns()
                .stream()
                .map(op -> {
                    var column = new MultipleChoiceGridColumn();

                    column.setColumnName(op.getColumnName());
                    column.setOrderIndex(op.getOrderIndex());
                    column.setMultipleChoiceGrid(multipleChoiceGrid);

                    return column;
                })
                .toList();

        multipleChoiceGrid.setRows(rows);
        multipleChoiceGrid.setColumns(columns);

        return multipleChoiceGrid;
    }

}
