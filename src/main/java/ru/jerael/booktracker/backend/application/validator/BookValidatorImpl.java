package ru.jerael.booktracker.backend.application.validator;

import ru.jerael.booktracker.backend.application.annotation.AppValidator;
import ru.jerael.booktracker.backend.domain.constant.AuthorRules;
import ru.jerael.booktracker.backend.domain.constant.BookRules;
import ru.jerael.booktracker.backend.domain.constant.LanguageRules;
import ru.jerael.booktracker.backend.domain.constant.PublisherRules;
import ru.jerael.booktracker.backend.domain.exception.ValidationException;
import ru.jerael.booktracker.backend.domain.exception.factory.BookErrorFactory;
import ru.jerael.booktracker.backend.domain.exception.factory.CommonValidationErrorFactory;
import ru.jerael.booktracker.backend.domain.exception.model.ValidationError;
import ru.jerael.booktracker.backend.domain.model.book.BookCreation;
import ru.jerael.booktracker.backend.domain.model.book.BookDetailsUpdate;
import ru.jerael.booktracker.backend.domain.validator.BookValidator;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@AppValidator
public class BookValidatorImpl implements BookValidator {
    @Override
    public void validateUpdate(BookDetailsUpdate data) {
        List<ValidationError> errors = new ArrayList<>();
        errors.addAll(validateTitle(data.title()));
        errors.addAll(validateAuthorNames(data.authorNames()));
        errors.addAll(validateDescription(data.description()));
        errors.addAll(validatePublisherName(data.publisherName()));
        errors.addAll(validateLanguageCode(data.languageCode()));
        errors.addAll(validatePublishedOn(data.publishedOn()));
        errors.addAll(validateTotalPages(data.totalPages()));
        errors.addAll(validateIsbn10(data.isbn10()));
        errors.addAll(validateIsbn13(data.isbn13()));
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    @Override
    public void validateCreation(BookCreation data) {
        List<ValidationError> errors = new ArrayList<>();
        errors.addAll(validateTitle(data.title()));
        errors.addAll(validateAuthorNames(data.authorNames()));
        errors.addAll(validateDescription(data.description()));
        errors.addAll(validatePublisherName(data.publisherName()));
        errors.addAll(validateLanguageCode(data.languageCode()));
        errors.addAll(validatePublishedOn(data.publishedOn()));
        errors.addAll(validateTotalPages(data.totalPages()));
        errors.addAll(validateIsbn10(data.isbn10()));
        errors.addAll(validateIsbn13(data.isbn13()));
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    private List<ValidationError> validateTitle(String title) {
        List<ValidationError> errors = new ArrayList<>();
        if (title == null) return errors;
        if (title.isBlank()) {
            errors.add(CommonValidationErrorFactory.emptyField("title"));
        }
        if (title.length() > BookRules.TITLE_MAX_LENGTH) {
            errors.add(CommonValidationErrorFactory.fieldTooLong("title", BookRules.TITLE_MAX_LENGTH));
        }
        return errors;
    }

    private List<ValidationError> validateAuthorNames(Set<String> authorNames) {
        List<ValidationError> errors = new ArrayList<>();
        if (authorNames == null) return errors;
        if (authorNames.isEmpty()) {
            errors.add(CommonValidationErrorFactory.emptyField("authorNames"));
            return errors;
        }
        if (authorNames.stream().anyMatch(String::isBlank)) {
            errors.add(CommonValidationErrorFactory.emptyListItem("authorNames"));
        }
        Set<String> longNames = authorNames.stream()
            .filter(name -> name.length() > AuthorRules.AUTHOR_FULL_NAME_MAX_LENGTH)
            .collect(Collectors.toSet());
        if (!longNames.isEmpty()) {
            errors.add(
                CommonValidationErrorFactory.listItemsTooLong(
                    "authorNames",
                    longNames,
                    AuthorRules.AUTHOR_FULL_NAME_MAX_LENGTH
                )
            );
        }
        return errors;
    }

    private List<ValidationError> validateDescription(String description) {
        List<ValidationError> errors = new ArrayList<>();
        if (description == null) return errors;
        if (description.isBlank()) {
            errors.add(CommonValidationErrorFactory.emptyField("description"));
        }
        if (description.length() > BookRules.DESCRIPTION_MAX_LENGTH) {
            errors.add(CommonValidationErrorFactory.fieldTooLong("description", BookRules.DESCRIPTION_MAX_LENGTH));
        }
        return errors;
    }

    private List<ValidationError> validatePublisherName(String publisherName) {
        List<ValidationError> errors = new ArrayList<>();
        if (publisherName == null) return errors;
        if (publisherName.isBlank()) {
            errors.add(CommonValidationErrorFactory.emptyField("publisherName"));
        }
        if (publisherName.length() > PublisherRules.PUBLISHER_NAME_MAX_LENGTH) {
            errors.add(
                CommonValidationErrorFactory.fieldTooLong(
                    "publisherName",
                    PublisherRules.PUBLISHER_NAME_MAX_LENGTH
                )
            );
        }
        return errors;
    }

    private List<ValidationError> validateLanguageCode(String languageCode) {
        List<ValidationError> errors = new ArrayList<>();
        if (languageCode == null) return errors;
        if (languageCode.isBlank()) {
            errors.add(CommonValidationErrorFactory.emptyField("languageCode"));
        }
        if (languageCode.length() > LanguageRules.LANGUAGE_CODE_LENGTH) {
            errors.add(
                CommonValidationErrorFactory.fieldTooLong(
                    "languageCode",
                    LanguageRules.LANGUAGE_CODE_LENGTH
                )
            );
        }
        return errors;
    }

    private List<ValidationError> validatePublishedOn(Integer publishedOn) {
        List<ValidationError> errors = new ArrayList<>();
        if (publishedOn == null) return errors;
        if (publishedOn < BookRules.PUBLISHED_ON_MIN || publishedOn > BookRules.PUBLISHED_ON_MAX) {
            errors.add(
                CommonValidationErrorFactory.fieldOutOfRange(
                    "publishedOn",
                    BookRules.PUBLISHED_ON_MIN,
                    BookRules.PUBLISHED_ON_MAX
                )
            );
        }
        return errors;
    }

    private List<ValidationError> validateTotalPages(Integer totalPages) {
        List<ValidationError> errors = new ArrayList<>();
        if (totalPages == null) return errors;
        if (totalPages <= 0) {
            errors.add(BookErrorFactory.invalidTotalPages());
        }
        return errors;
    }

    private List<ValidationError> validateIsbn10(String isbn10) {
        List<ValidationError> errors = new ArrayList<>();
        if (isbn10 == null) return errors;
        if (isbn10.isBlank()) {
            errors.add(CommonValidationErrorFactory.emptyField("isbn10"));
        }
        if (isbn10.length() != 10) {
            errors.add(CommonValidationErrorFactory.invalidLength("isbn10", 10));
        }
        return errors;
    }

    private List<ValidationError> validateIsbn13(String isbn13) {
        List<ValidationError> errors = new ArrayList<>();
        if (isbn13 == null) return errors;
        if (isbn13.isBlank()) {
            errors.add(CommonValidationErrorFactory.emptyField("isbn13"));
        }
        if (isbn13.length() != 13) {
            errors.add(CommonValidationErrorFactory.invalidLength("isbn13", 13));
        }
        return errors;
    }
}
