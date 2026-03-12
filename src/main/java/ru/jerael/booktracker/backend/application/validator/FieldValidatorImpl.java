package ru.jerael.booktracker.backend.application.validator;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Component;
import ru.jerael.booktracker.backend.domain.constant.UserRules;
import ru.jerael.booktracker.backend.domain.exception.factory.CommonValidationErrorFactory;
import ru.jerael.booktracker.backend.domain.exception.factory.EmailValidationErrorFactory;
import ru.jerael.booktracker.backend.domain.exception.factory.PasswordValidationErrorFactory;
import ru.jerael.booktracker.backend.domain.exception.model.ValidationError;
import ru.jerael.booktracker.backend.domain.validator.FieldValidator;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class FieldValidatorImpl implements FieldValidator {
    @Override
    public List<ValidationError> validateEmail(String email) {
        List<ValidationError> errors = new ArrayList<>();
        if (email == null || email.isBlank()) {
            errors.add(CommonValidationErrorFactory.emptyField("email"));
        } else {
            if (email.length() > UserRules.EMAIL_MAX_LENGTH) {
                errors.add(CommonValidationErrorFactory.fieldTooLong("email", UserRules.EMAIL_MAX_LENGTH));
            }
            if (!EmailValidator.getInstance().isValid(email)) {
                errors.add(EmailValidationErrorFactory.invalidFormat());
            }
        }
        return errors;
    }

    @Override
    public List<ValidationError> validatePassword(String password) {
        List<ValidationError> errors = new ArrayList<>();
        if (password == null || password.isBlank()) {
            errors.add(CommonValidationErrorFactory.emptyField("password"));
        } else {
            if (password.length() < UserRules.MIN_PASSWORD_LENGTH) {
                errors.add(CommonValidationErrorFactory.fieldTooShort("password", UserRules.MIN_PASSWORD_LENGTH));
            }
            if (password.chars().noneMatch(Character::isLowerCase)) {
                errors.add(PasswordValidationErrorFactory.needsLowercase());
            }
            if (password.chars().noneMatch(Character::isUpperCase)) {
                errors.add(PasswordValidationErrorFactory.needsUppercase());
            }
            if (password.chars().noneMatch(Character::isDigit)) {
                errors.add(PasswordValidationErrorFactory.needsDigit());
            }
            if (password.chars().noneMatch(c -> UserRules.PASSWORD_ALLOWED_SPECIAL_CHARS.contains((char) c))) {
                errors.add(PasswordValidationErrorFactory.needsSpecialChar(UserRules.PASSWORD_ALLOWED_SPECIAL_CHARS));
            }

            Set<Character> forbiddenChars = password.chars()
                .filter(c -> !Character.isLowerCase(c) &&
                    !Character.isUpperCase(c) &&
                    !Character.isDigit(c) &&
                    !UserRules.PASSWORD_ALLOWED_SPECIAL_CHARS.contains((char) c)
                )
                .mapToObj(c -> (char) c)
                .collect(Collectors.toSet());

            if (!forbiddenChars.isEmpty()) {
                errors.add(PasswordValidationErrorFactory.forbiddenChar(forbiddenChars,
                    UserRules.PASSWORD_ALLOWED_SPECIAL_CHARS));
            }
        }
        return errors;
    }

    @Override
    public List<ValidationError> validateRefreshToken(String refreshToken) {
        List<ValidationError> errors = new ArrayList<>();
        if (refreshToken == null || refreshToken.isBlank()) {
            errors.add(CommonValidationErrorFactory.emptyField("refreshToken"));
        }
        return errors;
    }
}
