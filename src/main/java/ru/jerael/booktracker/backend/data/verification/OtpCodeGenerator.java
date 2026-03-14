package ru.jerael.booktracker.backend.data.verification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.jerael.booktracker.backend.data.verification.config.OtpCodeProperties;
import ru.jerael.booktracker.backend.domain.model.verification.VerificationToken;
import ru.jerael.booktracker.backend.domain.verification.VerificationTokenGenerator;
import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class OtpCodeGenerator implements VerificationTokenGenerator {
    private final OtpCodeProperties properties;
    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    public VerificationToken generate() {
        StringBuilder code = new StringBuilder(properties.getLength());
        for (int i = 0; i < properties.getLength(); i++) {
            int digit = secureRandom.nextInt(10);
            code.append(digit);
        }
        return new VerificationToken(
            code.toString(),
            properties.getExpiry()
        );
    }
}
