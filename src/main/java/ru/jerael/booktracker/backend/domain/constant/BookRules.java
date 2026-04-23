package ru.jerael.booktracker.backend.domain.constant;

public final class BookRules {
    private BookRules() {}

    public static final int TITLE_MAX_LENGTH = 500;
    public static final int DESCRIPTION_MAX_LENGTH = 5000;
    public static final String COVER_FIELD_NAME = "cover";
    public static final int PUBLISHED_ON_MIN = -5000;
    public static final int PUBLISHED_ON_MAX = 3000;
}
