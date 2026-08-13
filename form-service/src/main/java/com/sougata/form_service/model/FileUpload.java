package com.sougata.form_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.List;

@Entity
@Table(name = "file_uploads")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FileUpload extends AnyTypeQuestion {

    @ManyToMany
    @JoinTable(
            name = "file_upload_file_type",
            joinColumns = @JoinColumn(
                    name = "file_upload_id", nullable = false
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "file_type_id", nullable = false
            )
    )
    private List<FileType> allowedFileTypes;

    @Column(nullable = false)
    private Integer maxFileSize;
}
