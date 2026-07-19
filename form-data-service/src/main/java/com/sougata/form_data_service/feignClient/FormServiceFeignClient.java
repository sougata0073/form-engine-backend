package com.sougata.form_data_service.feignClient;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("form-service")
public interface FormServiceFeignClient {

//    @PostMapping(path = "api/v1/forms/{formId}/validate-response")
//    SuccessMessageDto validateResponse(@PathVariable("formId") UUID formId, @RequestBody ResponseValidationRequestDto body);
//
//    @GetMapping(path = "api/v1/forms/{formId}/question-summaries")
//    QuestionSummariesResDto getQuestionSummaries(@PathVariable("formId") UUID formId);
//
//    @GetMapping(path = "api/v1/forms/{formId}/details")
//    FormResponseDto getFormDetails(@PathVariable("formId") UUID formId);
//
//    @GetMapping(path = "api/v1/forms/{formId}/questions/{questionId}/summary")
//    QuestionSummaryDto getQuestionSummary(@PathVariable("formId") UUID formId, @PathVariable("questionId") Long questionId);
//
//    @GetMapping(path = "api/v1/forms/{formId}/questions/{questionId}")
//    QuestionRes getQuestion(@PathVariable("formId") UUID formId, @PathVariable("questionId") Long questionId);
//
//    @GetMapping(path = "api/v1/forms/{formId}/info")
//    FormInfoResDto getFormInfo(@PathVariable("formId") UUID formId);
}
