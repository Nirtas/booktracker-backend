package ru.jerael.booktracker.backend.data.verification;

import org.junit.jupiter.api.Test;
import ru.jerael.booktracker.backend.data.verification.config.OtpCodeProperties;
import ru.jerael.booktracker.backend.domain.model.verification.VerificationToken;
import java.time.Duration;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class OtpCodeGeneratorTest {
    private final OtpCodeGenerator generator;

    private static final int CODE_LENGTH = 6;
    private static final Duration EXPIRY = Duration.ofSeconds(1L);

    public OtpCodeGeneratorTest() {
        OtpCodeProperties properties = new OtpCodeProperties();
        properties.setLength(CODE_LENGTH);
        properties.setExpiry(EXPIRY);
        generator = new OtpCodeGenerator(properties);
    }

    @Test
    void generate_ShouldReturnCodeWithCorrectLengthAndExpiry() {
        VerificationToken token = generator.generate();

        assertEquals(CODE_LENGTH, token.value().length());
        assertEquals(EXPIRY, token.expiry());
    }

    @Test
    void generate_ShouldContainsOnlyDigits() {
        VerificationToken token = generator.generate();

        assertThat(token.value()).containsOnlyDigits();
    }

    @Test
    void generate_ShouldReturnDifferentCodes() {
        VerificationToken token1 = generator.generate();
        VerificationToken token2 = generator.generate();

        assertNotEquals(token1.value(), token2.value());
    }
}