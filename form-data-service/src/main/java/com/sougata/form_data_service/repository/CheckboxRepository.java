package com.sougata.form_data_service.repository;

import com.sougata.form_data_service.model.Checkbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CheckboxRepository extends JpaRepository<Checkbox, Long> {
}
