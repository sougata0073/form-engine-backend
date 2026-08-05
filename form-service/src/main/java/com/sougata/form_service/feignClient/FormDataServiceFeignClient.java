package com.sougata.form_service.feignClient;

import com.sougata.form_service.dto.form.FormResponseSummaryShortDto;
import com.sougata.form_service.dto.question.QuestionSummaryDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient("form-data-service")
public interface FormDataServiceFeignClient {

    @GetMapping(path = "api/v1/forms/{formId}/is-response-already-submitted", params = "userId")
    boolean getIsResponseAlreadySubmitted(
            @PathVariable("formId") UUID formId,
            @RequestParam("userId") UUID userId
    );

    @DeleteMapping(path = "api/v1/forms/{formId}/questions/{questionId}/responses")
    void deleteResponses(@PathVariable("formId") UUID formId, @PathVariable("questionId") Long questionId);

    @GetMapping(path = "api/v1/forms/{formId}/response-summary")
    FormResponseSummaryShortDto getFormResponseSummary(@PathVariable("formId") UUID formId);

}
