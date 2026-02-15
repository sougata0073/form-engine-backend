package com.sougata.form_data_service.model;

import com.github.f4b6a3.tsid.TsidCreator;
import io.hypersistence.utils.hibernate.type.array.IntArrayType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "tick_box_grid_rows")
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TickBoxGridRow extends Auditable {

    @Id
    private Long id = TsidCreator.getTsid().toLong();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private TickBoxGrid tickBoxGrid;

    @Type(IntArrayType.class)
    @Column(columnDefinition = "integer[]", nullable = false)
    private Integer[] selectedIndexes;
}
