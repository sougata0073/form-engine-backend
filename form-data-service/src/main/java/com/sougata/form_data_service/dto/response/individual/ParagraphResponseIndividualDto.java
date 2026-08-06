package com.sougata.form_data_service.dto.response.individual;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParagraphResponseIndividualDto extends ResponseIndividualDto {
    private String text;
}
