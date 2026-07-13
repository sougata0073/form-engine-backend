package com.sougata.form_data_service.dto.validation.request;

import com.sougata.form_data_service.dto.question.request.QuestionResponseAddReq;

import java.util.List;

public record ResponseValidationRequestDto(

        List<QuestionResponseAddReq> responses

) {
}
