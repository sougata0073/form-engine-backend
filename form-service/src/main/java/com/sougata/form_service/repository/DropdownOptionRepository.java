package com.sougata.form_service.repository;

import com.sougata.form_service.model.questionSchema.DropdownOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
public interface DropdownOptionRepository extends JpaRepository<DropdownOption, Long> {

    @Modifying
    @Transactional
    @Query("delete from DropdownOption do where do.dropdown.question.form.id = :formId and do.dropdown.questionId = :dropdownId")
    void deleteAllByFormIdAndDropdownId(UUID formId, long dropdownId);

}