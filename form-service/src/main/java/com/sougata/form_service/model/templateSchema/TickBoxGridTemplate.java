package com.sougata.form_service.model.templateSchema;

import com.sougata.form_service.model.Form;
import com.sougata.form_service.model.questionSchema.TickBoxGrid;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tick_box_grid_templates")
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TickBoxGridTemplate extends QuestionTemplate<TickBoxGridTemplate> {

    @Column(nullable = false)
    private Boolean eachRowRequired;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "tickBoxGridTemplate")
    private List<TickBoxGridRowTemplate> rows = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "tickBoxGridTemplate")
    private List<TickBoxGridColumnTemplate> columns = new ArrayList<>();

    @Override
    public TickBoxGrid fromTemplate(Form f, TickBoxGridTemplate t) {
        var tickBoxGrid = new TickBoxGrid();

//        tickBoxGrid.setQuestion(t.getQuestion());
//        tickBoxGrid.setDescription(t.getDescription());
//        tickBoxGrid.setRequired(t.getRequired());
//        tickBoxGrid.setForm(f);
//        tickBoxGrid.setOrderIndex(t.getOrderIndex());
//        tickBoxGrid.setEachRowRequired(t.getEachRowRequired());
//
//        var rows = t.getRows()
//                .stream()
//                .map(op -> {
//                    var row = new TickBoxGridRow();
//
//                    row.setRowName(op.getRowName());
//                    row.setOrderIndex(op.getOrderIndex());
//                    row.setTickBoxGrid(tickBoxGrid);
//
//                    return row;
//                })
//                .toList();
//
//        var columns = t.getColumns()
//                .stream()
//                .map(op -> {
//                    var column = new TickBoxGridColumn();
//
//                    column.setColumnName(op.getColumnName());
//                    column.setOrderIndex(op.getOrderIndex());
//                    column.setTickBoxGrid(tickBoxGrid);
//
//                    return column;
//                })
//                .toList();
//
//        tickBoxGrid.setRows(rows);
//        tickBoxGrid.setColumns(columns);

        return tickBoxGrid;
    }
}
