package com.sougata.form_data_service.repository;

import com.sougata.form_data_service.model.MultipleChoiceGridRow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
public interface MultipleChoiceGridRowRepository extends JpaRepository<MultipleChoiceGridRow, Long> {

    @Modifying
    @Transactional
    @Query("delete from MultipleChoiceGridRow mcr where mcr.multipleChoiceGrid.questionResponse.formResponse.formId = :formId and mcr.multipleChoiceGrid.questionResponse.questionId = :questionId")
    void deleteAllByFormIdAndQuestionId(UUID formId, long questionId);

}
