package ru.jerael.booktracker.backend.data.verification;

import org.junit.jupiter.api.Test;
import ru.jerael.booktracker.backend.data.verification.config.OtpCodeProperties;
import ru.jerael.booktracker.backend.domain.model.verification.VerificationCode;
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
        VerificationCode code = generator.generate();

        assertEquals(CODE_LENGTH, code.value().length());
        assertEquals(EXPIRY, code.expiry());
    }

    @Test
    void generate_ShouldContainsOnlyDigits() {
        VerificationCode code = generator.generate();

        assertThat(code.value()).containsOnlyDigits();
    }

    @Test
    void generate_ShouldReturnDifferentCodes() {
        VerificationCode code1 = generator.generate();
        VerificationCode code2 = generator.generate();

        assertNotEquals(code1.value(), code2.value());
    }
}