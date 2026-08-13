package com.sougata.form_data_service.repository;

import com.sougata.form_data_service.model.TickBoxGridColumn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
public interface TickBoxGridColumnRepository extends JpaRepository<TickBoxGridColumn, Long> {

    @Modifying
    @Transactional
    @Query("delete from TickBoxGridColumn tbgc where tbgc.tickBoxGridRow.tickBoxGrid.questionResponse.formResponse.formId = :formId and tbgc.tickBoxGridRow.tickBoxGrid.questionResponse.questionId = :questionId")
    void deleteAllByFormIdAndQuestionId(UUID formId, long questionId);

    @Modifying
    @Transactional
    @Query("delete from TickBoxGridColumn tbgc where tbgc.tickBoxGridRow.tickBoxGrid.questionResponse.formResponse.formId = :formId and tbgc.tickBoxGridRow.tickBoxGrid.questionResponse.formResponse.id = :formResponseId")
    void deleteAllByFormIdAndFormResponseId(UUID formId, long formResponseId);

}
