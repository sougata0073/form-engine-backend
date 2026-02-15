package com.sougata.form_data_service.dto.form;

import com.sougata.form_data_service.dto.question.QuestionResponseAddReq;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record FormResponseAddReqDto(

        @NotNull
        UUID formId,

        @NotNull
        List<@Valid QuestionResponseAddReq> responses
) {
}
