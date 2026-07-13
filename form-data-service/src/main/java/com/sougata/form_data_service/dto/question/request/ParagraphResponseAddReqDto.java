package com.sougata.form_data_service.dto.question.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ParagraphResponseAddReqDto extends QuestionResponseAddReq {

    private String text;

}
