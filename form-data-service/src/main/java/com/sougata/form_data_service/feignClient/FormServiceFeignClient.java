package com.sougata.form_data_service.feignClient;

import com.sougata.form_data_service.dto.common.SuccessMessageDto;
import com.sougata.form_data_service.dto.form.FormResponseDto;
import com.sougata.form_data_service.dto.question.QuestionSummaryDto;
import com.sougata.form_data_service.dto.question.response.QuestionRes;
import com.sougata.form_data_service.dto.question.response.QuestionSummariesResDto;
import com.sougata.form_data_service.dto.validation.request.ResponseValidationRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient("form-service")
public interface FormServiceFeignClient {

    @PostMapping(path = "api/v1/forms/{formId}/validate-response")
    SuccessMessageDto validateResponse(@PathVariable("formId") UUID formId, @RequestBody ResponseValidationRequestDto body);

    @GetMapping(path = "api/v1/forms/{formId}/question-summaries")
    QuestionSummariesResDto getQuestionSummaries(@PathVariable("formId") UUID formId);

    @GetMapping(path = "api/v1/forms/{formId}/details")
    FormResponseDto getFormDetails(@PathVariable("formId") UUID formId);

    @GetMapping(path = "api/v1/forms/{formId}/questions/{questionId}/summary")
    QuestionSummaryDto getQuestionSummary(@PathVariable("formId") UUID formId, @PathVariable("questionId") Long questionId);

    @GetMapping(path = "api/v1/forms/{formId}/questions/{questionId}")
    QuestionRes getQuestion(@PathVariable("formId") UUID formId, @PathVariable("questionId") Long questionId);

}
