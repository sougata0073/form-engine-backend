package com.sougata.form_data_service.model;

import io.hypersistence.utils.hibernate.type.array.IntArrayType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "tick_box_grids")
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TickBoxGrid extends QuestionResponse {

    @Type(IntArrayType.class)
    @Column(nullable = false, columnDefinition = "integer[]")
    private Integer[][] rows;

}
