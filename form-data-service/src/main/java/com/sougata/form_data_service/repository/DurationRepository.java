package com.sougata.form_data_service.repository;

import com.sougata.form_data_service.model.Duration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DurationRepository extends JpaRepository<Duration, Long> {
}
