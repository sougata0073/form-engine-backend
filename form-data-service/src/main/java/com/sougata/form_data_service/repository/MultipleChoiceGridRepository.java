package com.sougata.form_data_service.repository;

import com.sougata.form_data_service.model.MultipleChoiceGrid;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository("MULTIPLE_CHOICE_GRID_RESPONSE_REPOSITORY")
public interface MultipleChoiceGridRepository extends QuestionResponseRepository<MultipleChoiceGrid, Long> {

    @Query("""
            select
            mcg.questionId questionId,
            mcgr.rowId rowId,
            mcgr.responseColumnId responseColumnId,
            count(mcgr.responseColumnId) responseCount
            from MultipleChoiceGridRow mcgr
            join mcgr.multipleChoiceGrid mcg
            where mcg.formResponse.formId = :formId
            group by mcg.questionId, mcgr.rowId, mcgr.responseColumnId
            """)
    List<Tuple> getResponseOptionCount(UUID formId);

    @Query(value = """
            select
                mcgr.row_id as rowId,
                mcgr.response_column_id columnId,
                count(*) as responseCount,
                array_agg(fr.id order by fr.created_at) as responseIds
            from multiple_choice_grids mcg
            join multiple_choice_grid_rows mcgr
                on mcg.id = mcgr.multiple_choice_grid_id
            join form_responses fr
                on fr.id = mcg.form_response_id
            where mcg.question_id = :questionId
              and fr.form_id = :formId
            group by mcgr.row_id, mcgr.response_column_id
            order by responseCount desc
            """, nativeQuery = true)
    List<Tuple> groupedByResponseRowColumn(UUID formId, Long questionId);
}
