package com.sougata.form_service.repository;

import com.sougata.form_service.model.Form;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FormRepository extends JpaRepository<Form, UUID> {

    Integer findQuestionCountById(UUID id);
}
