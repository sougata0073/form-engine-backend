package com.sougata.form_data_service.repository;

import com.sougata.form_data_service.model.TickBoxGridRow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
public interface TickBoxGridRowRepository extends JpaRepository<TickBoxGridRow, Long> {

    @Modifying
    @Transactional
    @Query("delete from TickBoxGridRow tbgr where tbgr.tickBoxGrid.questionResponse.formResponse.formId = :formId and tbgr.tickBoxGrid.questionResponse.questionId = :questionId")
    void deleteAllByFormIdAndQuestionId(UUID formId, long questionId);

}
