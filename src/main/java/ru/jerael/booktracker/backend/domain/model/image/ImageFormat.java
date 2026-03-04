package ru.jerael.booktracker.backend.domain.model.image;

public enum ImageFormat {
    JPG, JPEG, PNG, WEBP, BMP;

    public String getExtension() {
        return this.name().toLowerCase();
    }
}
