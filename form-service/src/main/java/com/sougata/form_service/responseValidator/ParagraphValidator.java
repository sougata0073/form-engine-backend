package com.sougata.form_service.responseValidator;

import com.sougata.form_service.constant.ValidationMessages;
import com.sougata.form_service.dto.validation.request.ParagraphValidationRequestDto;
import com.sougata.form_service.dto.validationConfig.NoneValidationConfig;
import com.sougata.form_service.dto.validationConfig.ParagraphValidationConfig;
import com.sougata.form_service.exception.ResponseValidationException;

public class ParagraphValidator {

    public static class LengthMaxCharacterCount implements
            ResponseValidator<ParagraphValidationRequestDto, ParagraphValidationConfig.MaxCharacterCount> {

        @Override
        public boolean isValid(ParagraphValidationRequestDto validationRequestDto, ParagraphValidationConfig.MaxCharacterCount validationConfig) {
            if (validationRequestDto.getText().length() > validationConfig.getNumber()) {
                throw new ResponseValidationException(
                        String.format(
                                ValidationMessages.INVALID_MAX_CHARACTER_LENGTH, validationConfig.getNumber()
                        )
                );
            }

            return true;
        }

        @Override
        public Class<ParagraphValidationRequestDto> getValidationRequestClass() {
            return ParagraphValidationRequestDto.class;
        }

        @Override
        public Class<ParagraphValidationConfig.MaxCharacterCount> getValidationConfigClass() {
            return ParagraphValidationConfig.MaxCharacterCount.class;
        }
    }

    public static class LengthMinCharacterCount implements
            ResponseValidator<ParagraphValidationRequestDto, ParagraphValidationConfig.MinCharacterCount> {

        @Override
        public boolean isValid(ParagraphValidationRequestDto validationRequestDto, ParagraphValidationConfig.MinCharacterCount validationConfig) {
            if (validationRequestDto.getText().length() < validationConfig.getNumber()) {
                throw new ResponseValidationException(
                        String.format(
                                ValidationMessages.INVALID_MIN_CHARACTER_LENGTH, validationConfig.getNumber()
                        )
                );
            }

            return true;
        }

        @Override
        public Class<ParagraphValidationRequestDto> getValidationRequestClass() {
            return ParagraphValidationRequestDto.class;
        }

        @Override
        public Class<ParagraphValidationConfig.MinCharacterCount> getValidationConfigClass() {
            return ParagraphValidationConfig.MinCharacterCount.class;
        }
    }

    public static class RegexMatches implements
            ResponseValidator<ParagraphValidationRequestDto, ParagraphValidationConfig.Matches> {

        @Override
        public boolean isValid(ParagraphValidationRequestDto validationRequestDto, ParagraphValidationConfig.Matches validationConfig) {
            String pattern = validationConfig.getText();
            if (pattern == null) {
                pattern = "";
            }

            if (validationRequestDto.getText() == null || !validationRequestDto.getText().matches(pattern)) {
                throw new ResponseValidationException(ValidationMessages.INVALID_REGEX_MATCH);
            }

            return true;
        }

        @Override
        public Class<ParagraphValidationRequestDto> getValidationRequestClass() {
            return ParagraphValidationRequestDto.class;
        }

        @Override
        public Class<ParagraphValidationConfig.Matches> getValidationConfigClass() {
            return ParagraphValidationConfig.Matches.class;
        }
    }

    public static class DoesNotMatch implements
            ResponseValidator<ParagraphValidationRequestDto, ParagraphValidationConfig.DoesNotMatch> {

        @Override
        public boolean isValid(ParagraphValidationRequestDto validationRequestDto, ParagraphValidationConfig.DoesNotMatch validationConfig) {
            String pattern = validationConfig.getText();
            if (pattern == null) {
                pattern = "";
            }

            if (validationRequestDto.getText() != null && validationRequestDto.getText().matches(pattern)) {
                throw new ResponseValidationException(ValidationMessages.INVALID_REGEX_NOT_MATCH);
            }

            return true;
        }

        @Override
        public Class<ParagraphValidationRequestDto> getValidationRequestClass() {
            return ParagraphValidationRequestDto.class;
        }

        @Override
        public Class<ParagraphValidationConfig.DoesNotMatch> getValidationConfigClass() {
            return ParagraphValidationConfig.DoesNotMatch.class;
        }
    }

    public static class None implements ResponseValidator<ParagraphValidationRequestDto, NoneValidationConfig> {

        @Override
        public boolean isValid(ParagraphValidationRequestDto validationRequestDto, NoneValidationConfig validationConfig) {
            return true;
        }

        @Override
        public Class<ParagraphValidationRequestDto> getValidationRequestClass() {
            return ParagraphValidationRequestDto.class;
        }

        @Override
        public Class<NoneValidationConfig> getValidationConfigClass() {
            return NoneValidationConfig.class;
        }
    }

}
