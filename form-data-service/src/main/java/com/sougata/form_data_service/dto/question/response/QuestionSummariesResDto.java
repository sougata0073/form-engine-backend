package com.sougata.form_data_service.dto.question.response;

import com.sougata.form_data_service.dto.question.QuestionSummaryDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionSummariesResDto {
    private List<QuestionSummaryDto> questions;
}
