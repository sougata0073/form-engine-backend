package com.sougata.form_data_service.dto.question.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DateResponseAddReqDto extends QuestionResponseAddReq {

    private Instant date;

}
