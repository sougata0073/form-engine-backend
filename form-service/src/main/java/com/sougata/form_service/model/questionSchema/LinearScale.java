package com.sougata.form_service.model.questionSchema;

import com.github.f4b6a3.tsid.TsidCreator;
import com.sougata.form_service.model.Auditable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "linear_scales")
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LinearScale extends AnyTypeQuestion {

    @Column(nullable = false)
    private Integer fromNumber;

    @Column(nullable = false)
    private Integer toNumber;
}
