package com.sougata.form_service.model.templateSchema;

import com.sougata.form_service.model.FileType;
import com.sougata.form_service.model.Form;
import com.sougata.form_service.model.questionSchema.FileUpload;
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
public class FileUploadTemplate extends QuestionTemplate<FileUploadTemplate> {

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
    private List<FileType> allowedFileTypes;

    @Column(nullable = false)
    private Integer maxFileSize;

    @Override
    public FileUpload fromTemplate(Form f, FileUploadTemplate t) {
        var fileUpload = new FileUpload();

        fileUpload.setQuestion(t.getQuestion());
        fileUpload.setDescription(t.getDescription());
        fileUpload.setRequired(t.getRequired());
        fileUpload.setForm(f);
        fileUpload.setOrderIndex(t.getOrderIndex());
        fileUpload.setAllowedFileTypes(t.getAllowedFileTypes());
        fileUpload.setMaxFileSize(t.getMaxFileSize());

        return fileUpload;
    }
}
