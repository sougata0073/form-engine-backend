package com.sougata.form_service.repository;

import com.sougata.form_service.model.questionSchema.MultipleChoiceGridColumn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
public interface MultipleChoiceGridColumnRepository extends JpaRepository<MultipleChoiceGridColumn, Long> {

    @Modifying
    @Transactional
    @Query("delete from MultipleChoiceGridColumn mcgc where mcgc.multipleChoiceGrid.question.form.id = :formId and mcgc.multipleChoiceGrid.questionId = :multipleChoiceGridId")
    void deleteAllByFormIdAndMultipleChoiceGridId(UUID formId, long multipleChoiceGridId);

}
