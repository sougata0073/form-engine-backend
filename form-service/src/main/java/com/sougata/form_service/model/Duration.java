package com.sougata.form_service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "durations")
public class Duration extends AnyTypeQuestion {
}
