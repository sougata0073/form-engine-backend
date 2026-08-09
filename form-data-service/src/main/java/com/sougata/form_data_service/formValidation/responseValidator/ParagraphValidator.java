package com.sougata.form_data_service.formValidation.responseValidator;

import com.sougata.form_data_service.constant.ValidationMessages;
import com.sougata.form_data_service.dto.question.request.ParagraphResponseAddReqDto;
import com.sougata.form_data_service.dto.validationConfig.NoneValidationConfig;
import com.sougata.form_data_service.dto.validationConfig.ParagraphValidationConfig;
import com.sougata.form_data_service.formValidation.exception.ResponseValidationException;

public class ParagraphValidator {

    public static class LengthMaxCharacterCount implements
            ResponseValidator<ParagraphResponseAddReqDto, ParagraphValidationConfig.MaxCharacterCount> {

        @Override
        public boolean isValid(ParagraphResponseAddReqDto validationRequestDto, ParagraphValidationConfig.MaxCharacterCount validationConfig) {
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
        public Class<ParagraphResponseAddReqDto> getValidationRequestClass() {
            return ParagraphResponseAddReqDto.class;
        }

        @Override
        public Class<ParagraphValidationConfig.MaxCharacterCount> getValidationConfigClass() {
            return ParagraphValidationConfig.MaxCharacterCount.class;
        }
    }

    public static class LengthMinCharacterCount implements
            ResponseValidator<ParagraphResponseAddReqDto, ParagraphValidationConfig.MinCharacterCount> {

        @Override
        public boolean isValid(ParagraphResponseAddReqDto validationRequestDto, ParagraphValidationConfig.MinCharacterCount validationConfig) {
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
        public Class<ParagraphResponseAddReqDto> getValidationRequestClass() {
            return ParagraphResponseAddReqDto.class;
        }

        @Override
        public Class<ParagraphValidationConfig.MinCharacterCount> getValidationConfigClass() {
            return ParagraphValidationConfig.MinCharacterCount.class;
        }
    }

    public static class RegexMatches implements
            ResponseValidator<ParagraphResponseAddReqDto, ParagraphValidationConfig.Matches> {

        @Override
        public boolean isValid(ParagraphResponseAddReqDto validationRequestDto, ParagraphValidationConfig.Matches validationConfig) {
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
        public Class<ParagraphResponseAddReqDto> getValidationRequestClass() {
            return ParagraphResponseAddReqDto.class;
        }

        @Override
        public Class<ParagraphValidationConfig.Matches> getValidationConfigClass() {
            return ParagraphValidationConfig.Matches.class;
        }
    }

    public static class DoesNotMatch implements
            ResponseValidator<ParagraphResponseAddReqDto, ParagraphValidationConfig.DoesNotMatch> {

        @Override
        public boolean isValid(ParagraphResponseAddReqDto validationRequestDto, ParagraphValidationConfig.DoesNotMatch validationConfig) {
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
        public Class<ParagraphResponseAddReqDto> getValidationRequestClass() {
            return ParagraphResponseAddReqDto.class;
        }

        @Override
        public Class<ParagraphValidationConfig.DoesNotMatch> getValidationConfigClass() {
            return ParagraphValidationConfig.DoesNotMatch.class;
        }
    }

    public static class None implements ResponseValidator<ParagraphResponseAddReqDto, NoneValidationConfig> {

        @Override
        public boolean isValid(ParagraphResponseAddReqDto validationRequestDto, NoneValidationConfig validationConfig) {
            return true;
        }

        @Override
        public Class<ParagraphResponseAddReqDto> getValidationRequestClass() {
            return ParagraphResponseAddReqDto.class;
        }

        @Override
        public Class<NoneValidationConfig> getValidationConfigClass() {
            return NoneValidationConfig.class;
        }
    }

}
