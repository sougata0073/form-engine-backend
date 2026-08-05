package com.sougata.form_service.repository;

import com.sougata.form_service.model.questionSchema.MultipleChoiceOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
public interface MultipleChoiceOptionRepository extends JpaRepository<MultipleChoiceOption, Long> {

    @Modifying
    @Transactional
    @Query("delete from MultipleChoiceOption mco where mco.multipleChoice.question.form.id = :formId and mco.multipleChoice.questionId = :multipleChoiceId")
    void deleteAllByFormIdAndMultipleChoiceId(UUID formId, long multipleChoiceId);

}
