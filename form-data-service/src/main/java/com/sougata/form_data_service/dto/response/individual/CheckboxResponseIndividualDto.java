package com.sougata.form_data_service.dto.response.individual;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CheckboxResponseIndividualDto extends ResponseIndividualDto {
    private List<String> optionIds;
}
