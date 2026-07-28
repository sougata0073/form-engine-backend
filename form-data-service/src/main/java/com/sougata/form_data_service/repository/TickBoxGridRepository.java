package com.sougata.form_data_service.repository;

import com.sougata.form_data_service.model.TickBoxGrid;
import jakarta.persistence.Tuple;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository("TICK_BOX_GRID_RESPONSE_REPOSITORY")
public interface TickBoxGridRepository extends AnyTypeQuestionResponseRepository<TickBoxGrid, Long> {

    @Query("""
            select
                tbg.questionResponse.questionId questionId,
                tbgr.rowId rowId,
                tbgc.responseOptionId responseOptionId,
                count(tbgc.responseOptionId) responseCount
            from TickBoxGridColumn tbgc
            join tbgc.tickBoxGridRow tbgr
            join tbgr.tickBoxGrid tbg
            where tbg.questionResponse.formResponse.formId = :formId
            group by
                tbg.questionResponse.questionId,
                tbgr.rowId,
                tbgc.responseOptionId
            order by
                tbgr.rowId,
                tbgc.responseOptionId
            """)
    List<Tuple> getResponseOptionCount(UUID formId);

    @Query(value = """
        select
            columnIds,
            count(*) as responseCount,
            array_agg(responseId order by createdAt) as responseIds
        from (
            select
                fr.id as responseId,
                fr.created_at as createdAt,
                array_agg(tbgc.response_option_id order by tbgc.response_option_id) as columnIds
            from tick_box_grids tbg
            join tick_box_grid_rows tbgr
                on tbgr.tick_box_grid_id = tbg.question_response_id
            join tick_box_grid_columns tbgc
                on tbgc.tick_box_grid_row_id = tbgr.id
            join question_responses qr
                on qr.id = tbg.question_response_id
            join form_responses fr
                on fr.id = qr.form_response_id
            where qr.question_id = :questionId
              and fr.form_id = :formId
              and tbgr.row_id = :rowId
            group by fr.id, fr.created_at
        ) x
        group by columnIds
        order by responseCount desc
        """, nativeQuery = true)
    List<Tuple> groupedByResponseRowColumn(UUID formId, long questionId, long rowId, Pageable pageable);
}
