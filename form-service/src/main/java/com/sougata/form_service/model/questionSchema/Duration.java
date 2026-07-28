package com.sougata.form_service.model.questionSchema;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "durations")
@EntityListeners(AuditingEntityListener.class)
public class Duration extends AnyTypeQuestion {
}
