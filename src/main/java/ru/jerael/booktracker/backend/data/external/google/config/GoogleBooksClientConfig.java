package ru.jerael.booktracker.backend.data.external.google.config;

import kotlin.text.Charsets;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import ru.jerael.booktracker.backend.data.exception.factory.ExternalApiExceptionFactory;
import ru.jerael.booktracker.backend.data.external.google.GoogleBooksClient;

@Configuration
@RequiredArgsConstructor
public class GoogleBooksClientConfig {
    private final GoogleBooksProperties properties;

    @Bean
    public GoogleBooksClient googleBooksClient() {
        RestClient restClient = RestClient.builder()
            .baseUrl(properties.getBaseUrl())
            .defaultStatusHandler(HttpStatusCode::isError, (request, response) -> {
                byte[] bodyBytes = response.getBody().readAllBytes();
                String body = new String(bodyBytes, Charsets.UTF_8);
                throw ExternalApiExceptionFactory.error(response.getStatusText(), new Throwable(body));
            })
            .build();

        RestClientAdapter adapter = RestClientAdapter.create(restClient);

        HttpServiceProxyFactory factory = HttpServiceProxyFactory
            .builderFor(adapter)
            .build();

        return factory.createClient(GoogleBooksClient.class);
    }
}
