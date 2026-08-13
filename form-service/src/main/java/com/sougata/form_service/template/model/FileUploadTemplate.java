package com.sougata.form_service.template.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.List;

@Entity
@Table(name = "file_upload_templates")
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FileUploadTemplate extends AnyQuestionTemplateType {

    @ManyToMany
    @JoinTable(
            name = "file_upload_template_file_type",
            joinColumns = @JoinColumn(
                    name = "file_upload_template_id", nullable = false
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "file_type_id", nullable = false
            )
    )
    private List<FileTypeTemplate> allowedFileTypeTemplates;

    @Column(nullable = false)
    private Integer maxFileSize;
}
