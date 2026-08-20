package com.sougata.form_service.template.model;

import io.hypersistence.utils.hibernate.type.array.StringArrayType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "file_type_templates")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class FileTypeTemplate {

    @Id
    private String category;

    @Type(StringArrayType.class)
    @Column(nullable = false, columnDefinition = "text[]")
    private String[] mimeTypes;
}
