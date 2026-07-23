package com.sougata.form_service.model.questionSchema;

import jakarta.persistence.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "dates")
@EntityListeners(AuditingEntityListener.class)
public class Date extends AnyTypeQuestion {

}
