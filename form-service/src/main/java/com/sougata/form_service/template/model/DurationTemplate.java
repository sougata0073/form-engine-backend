package com.sougata.form_service.template.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "duration_templates")
@EntityListeners(AuditingEntityListener.class)
public class DurationTemplate extends AnyTypeQuestionTemplate {
}
