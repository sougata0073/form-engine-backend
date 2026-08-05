package com.sougata.form_data_service.repository;

import com.sougata.form_data_service.model.CheckboxOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
public interface CheckboxOptionRepository extends JpaRepository<CheckboxOption, Long> {

    @Modifying
    @Transactional
    @Query("delete from CheckboxOption co where co.checkbox.questionResponse.formResponse.formId = :formId and co.checkbox.questionResponse.questionId = :questionId")
    void deleteAllByFormIdAndQuestionId(UUID formId, long questionId);

}
