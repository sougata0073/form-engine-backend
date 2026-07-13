package com.sougata.form_data_service.repository;

import com.sougata.form_data_service.model.TickBoxGrid;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository("TICK_BOX_GRID_RESPONSE_REPOSITORY")
public interface TickBoxGridRepository extends QuestionResponseRepository<TickBoxGrid, Long> {

    @Query("""
            select
                tbg.questionId questionId,
                tbgr.rowId rowId,
                tbgc.responseOptionId responseOptionId,
                count(tbgc.responseOptionId) responseCount
            from TickBoxGridColumn tbgc
            join tbgc.tickBoxGridRow tbgr
            join tbgr.tickBoxGrid tbg
            where tbg.formResponse.formId = :formId
            group by
                tbg.questionId,
                tbgr.rowId,
                tbgc.responseOptionId
            order by
                tbgr.rowId,
                tbgc.responseOptionId
            """)
    List<Tuple> getResponseOptionCount(UUID formId);

}
