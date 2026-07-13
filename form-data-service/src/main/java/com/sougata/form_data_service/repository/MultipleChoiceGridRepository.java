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

}
