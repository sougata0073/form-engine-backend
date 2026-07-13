package com.sougata.form_service.responseValidator;

import com.sougata.form_service.constant.ExceptionMessages;
import com.sougata.form_service.dto.validation.request.CheckboxValidationRequestDto;
import com.sougata.form_service.dto.validationConfig.CheckboxValidationConfig;
import com.sougata.form_service.dto.validationConfig.NoneValidationConfig;
import com.sougata.form_service.exception.ResponseValidationException;

public class CheckboxValidator {

    public static class SelectAtLeast implements
            ResponseValidator<CheckboxValidationRequestDto, CheckboxValidationConfig.SelectAtLeast> {

        @Override
        public boolean isValid(CheckboxValidationRequestDto validationRequestDto, CheckboxValidationConfig.SelectAtLeast validationConfig) {
            if (validationRequestDto.getResponseOptionIds().size() < validationConfig.getNumber()) {
                throw new ResponseValidationException(
                        String.format(
                                ExceptionMessages.INVALID_SELECT_AT_LEAST,
                                validationConfig.getNumber(),
                                validationRequestDto.getResponseOptionIds().size()
                        )
                );
            }

            return true;
        }

        @Override
        public Class<CheckboxValidationRequestDto> getValidationRequestClass() {
            return CheckboxValidationRequestDto.class;
        }

        @Override
        public Class<CheckboxValidationConfig.SelectAtLeast> getValidationConfigClass() {
            return CheckboxValidationConfig.SelectAtLeast.class;
        }
    }

    public static class SelectAtMost implements
            ResponseValidator<CheckboxValidationRequestDto, CheckboxValidationConfig.SelectAtMost> {

        @Override
        public boolean isValid(CheckboxValidationRequestDto validationRequestDto, CheckboxValidationConfig.SelectAtMost validationConfig) {
            if (validationRequestDto.getResponseOptionIds().size() > validationConfig.getNumber()) {
                throw new ResponseValidationException(
                        String.format(
                                ExceptionMessages.INVALID_SELECT_AT_MOST,
                                validationConfig.getNumber(),
                                validationRequestDto.getResponseOptionIds().size()
                        )
                );
            }

            return true;
        }

        @Override
        public Class<CheckboxValidationRequestDto> getValidationRequestClass() {
            return CheckboxValidationRequestDto.class;
        }

        @Override
        public Class<CheckboxValidationConfig.SelectAtMost> getValidationConfigClass() {
            return CheckboxValidationConfig.SelectAtMost.class;
        }
    }

    public static class SelectExactly implements
            ResponseValidator<CheckboxValidationRequestDto, CheckboxValidationConfig.SelectExactly> {

        @Override
        public boolean isValid(CheckboxValidationRequestDto validationRequestDto, CheckboxValidationConfig.SelectExactly validationConfig) {
            if (validationRequestDto.getResponseOptionIds().size() != validationConfig.getNumber()) {
                throw new ResponseValidationException(
                        String.format(
                                ExceptionMessages.INVALID_SELECT_EXACTLY,
                                validationConfig.getNumber(),
                                validationRequestDto.getResponseOptionIds().size()
                        )
                );
            }

            return true;
        }

        @Override
        public Class<CheckboxValidationRequestDto> getValidationRequestClass() {
            return CheckboxValidationRequestDto.class;
        }

        @Override
        public Class<CheckboxValidationConfig.SelectExactly> getValidationConfigClass() {
            return CheckboxValidationConfig.SelectExactly.class;
        }
    }

    public static class None implements ResponseValidator<CheckboxValidationRequestDto, NoneValidationConfig> {

        @Override
        public boolean isValid(CheckboxValidationRequestDto validationRequestDto, NoneValidationConfig validationConfig) {
            return true;
        }

        @Override
        public Class<CheckboxValidationRequestDto> getValidationRequestClass() {
            return CheckboxValidationRequestDto.class;
        }

        @Override
        public Class<NoneValidationConfig> getValidationConfigClass() {
            return NoneValidationConfig.class;
        }
    }

}
