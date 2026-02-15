package com.sougata.form_service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "dates")
@EntityListeners(AuditingEntityListener.class)
public class Date extends Question {

}
