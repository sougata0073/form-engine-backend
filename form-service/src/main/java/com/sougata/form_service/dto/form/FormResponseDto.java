package com.sougata.form_service.dto.form;

import com.sougata.form_service.dto.question.response.QuestionRes;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record FormResponseDto(
        UUID id,
        String title,
        String description,
        Boolean published,
        Boolean acceptingResponse,
        String notAcceptingResponseMessage,
        Instant stopAcceptingResponseOn,
        Long stopAcceptingResponseAfterResponse,
        List<QuestionRes> questions
) {
}
