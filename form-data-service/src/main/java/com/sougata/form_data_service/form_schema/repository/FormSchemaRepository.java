package com.sougata.form_data_service.form_schema.repository;

import com.sougata.form_data_service.form_schema.model.FormSchema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FormSchemaRepository extends JpaRepository<FormSchema, UUID> {
}
