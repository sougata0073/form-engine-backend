package com.sougata.form_data_service.repository;

import com.sougata.form_data_service.model.Paragraph;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository("PARAGRAPH_RESPONSE_REPOSITORY")
public interface ParagraphRepository extends QuestionResponseRepository<Paragraph, Long> {

    @Query("select p.questionId questionId, p.text text from Paragraph p where p.formResponse.formId = :formId")
    List<Tuple> getResponseTexts(UUID formId);

    @Query(value = """
            select
            p.text text,
            count(p.id) responseCount,
            array_agg(fr.id order by fr.created_at) responseIds
            from paragraphs p
            join form_responses fr
            on p.form_response_id = fr.id
            where fr.form_id = :formId and p.question_id = :questionId
            group by p.text
            """, nativeQuery = true)
    List<Tuple> groupedByText(UUID formId, Long questionId);
}
