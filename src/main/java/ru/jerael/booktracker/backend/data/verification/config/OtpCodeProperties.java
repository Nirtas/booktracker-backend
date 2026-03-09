package ru.jerael.booktracker.backend.data.verification.config;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import ru.jerael.booktracker.backend.domain.constant.EmailVerificationRules;
import java.time.Duration;

@Component
@Data
@ConfigurationProperties(prefix = "app.otp")
@Validated
public class OtpCodeProperties {
    @Min(EmailVerificationRules.TOKEN_MIN_LENGTH)
    @Max(EmailVerificationRules.TOKEN_MAX_LENGTH)
    private int length = 6;

    private Duration expiry = Duration.ofMinutes(10);
}
