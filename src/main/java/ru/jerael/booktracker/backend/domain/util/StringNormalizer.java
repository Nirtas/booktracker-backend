package ru.jerael.booktracker.backend.domain.util;

public class StringNormalizer {
    public static String normalize(String s) {
        if (s == null) return null;

        return s.trim().toLowerCase();
    }
}
