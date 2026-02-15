package com.sougata.form_data_service.repository;

import com.sougata.form_data_service.model.Dropdown;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DropdownRepository extends JpaRepository<Dropdown, Long> {
}
