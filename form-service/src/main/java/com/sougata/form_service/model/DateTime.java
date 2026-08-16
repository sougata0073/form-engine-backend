package com.sougata.form_service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "date_times")
public class DateTime extends AnyTypeQuestion {
}
