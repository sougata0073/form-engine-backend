package com.sougata.form_data_service.dto.form;

import com.sougata.form_data_service.dto.question.request.QuestionResponseAddReq;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record FormResponseAddReqDto(

        @NotNull
        List<@Valid QuestionResponseAddReq> responses
) {
}
