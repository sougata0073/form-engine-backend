package com.sougata.form_data_service.form_schema.model;

import io.hypersistence.utils.hibernate.type.array.StringArrayType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "file_types")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class FileType extends Auditable {

    @Id
    private String category;

    @Type(StringArrayType.class)
    @Column(nullable = false, columnDefinition = "text[]")
    private String[] mimeTypes;

    @ManyToMany(mappedBy = "allowedFileTypes")
    private List<FileUploadSchema> fileUploadSchemas = new ArrayList<>();

//    @ManyToMany(mappedBy = "allowedFileTypes")
//    private List<FileUploadTemplate> fileUploadTemplates = new ArrayList<>();
}
