package com.sougata.form_data_service.form_schema.repository;

import com.sougata.form_data_service.form_schema.model.FileType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileTypeRepository extends JpaRepository<FileType, String> {
}
