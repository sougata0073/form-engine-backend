package com.sougata.form_service.repository;

import com.sougata.form_service.model.TickBoxGridRow;
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
    @Query("delete from TickBoxGridRow tbg where tbg.tickBoxGrid.question.form.id = :formId and tbg.tickBoxGrid.questionId = :tickBoxGridId")
    void deleteAllByFormIdAndTickBoxGridId(UUID formId, long tickBoxGridId);

}
