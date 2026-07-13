package com.sougata.form_service.feignClient;

import com.sougata.form_service.constant.QuestionType;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient("form-data-service")
public interface FormDataServiceFeignClient {

    @GetMapping(path = "api/v1/forms/{formId}/is-response-already-submitted", params = "userId")
    boolean getIsResponseAlreadySubmitted(
            @PathVariable("formId") UUID formId,
            @RequestParam("userId") UUID userId
    );

    @DeleteMapping(path = "api/v1/forms/{formId}", params = "questionId")
    void deleteResponses(
            @PathVariable("formId") UUID formId,
            @RequestParam("questionId") Long questionId,
            @RequestParam("questionType") QuestionType questionType
    );

}
