package com.sougata.form_service.dto.form;

import com.sougata.form_service.dto.question.response.QuestionRes;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record FormResponseDto(
        UUID id,
        String name,
        String title,
        String description,
        Boolean published,
        Boolean acceptingResponse,
        String notAcceptingResponseMessage,
        Instant stopAcceptingResponseOn,
        Integer stopAcceptingResponseAfterResponse,
        Instant lastOpenedOn,
        List<QuestionRes> questions
) {
}
