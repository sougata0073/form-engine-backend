package com.sougata.form_service.model.questionSchema;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.f4b6a3.tsid.TsidCreator;
import com.sougata.form_service.model.Auditable;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "paragraphs")
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Paragraph extends AnyTypeQuestion {

    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private JsonNode validationConfig;
}
