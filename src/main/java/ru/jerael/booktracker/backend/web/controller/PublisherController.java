package ru.jerael.booktracker.backend.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.jerael.booktracker.backend.domain.model.pagination.PageQuery;
import ru.jerael.booktracker.backend.domain.model.pagination.PageResult;
import ru.jerael.booktracker.backend.domain.model.publisher.Publisher;
import ru.jerael.booktracker.backend.domain.usecase.publisher.SearchPublishersUseCase;
import ru.jerael.booktracker.backend.web.dto.publisher.PublisherResponse;
import ru.jerael.booktracker.backend.web.mapper.PublisherWebMapper;
import java.util.List;

@Tag(name = "Publishers")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/publishers")
public class PublisherController {
    private final SearchPublishersUseCase searchPublishersUseCase;
    private final PublisherWebMapper publisherWebMapper;

    @Operation(description = "Get publisher suggestions")
    @GetMapping("/autocomplete")
    public List<PublisherResponse> autocomplete(@RequestParam String query, @RequestParam int limit) {
        PageQuery pageQuery = new PageQuery(0, limit, null, null);
        PageResult<Publisher> result = searchPublishersUseCase.execute(pageQuery, query);
        return publisherWebMapper.toResponses(result);
    }
}
