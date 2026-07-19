package com.sougata.form_data_service.form_schema.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "date_times")
@EntityListeners(AuditingEntityListener.class)
public class DateTimeSchema extends QuestionSchema {


}
