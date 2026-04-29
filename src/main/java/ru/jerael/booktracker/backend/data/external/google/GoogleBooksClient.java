package ru.jerael.booktracker.backend.data.external.google;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import ru.jerael.booktracker.backend.data.external.google.dto.GoogleBooksResponse;

@HttpExchange
public interface GoogleBooksClient {

    @GetExchange(url = "/volumes")
    GoogleBooksResponse findBook(@RequestParam("q") String query, @RequestParam("key") String apiKey);
}
