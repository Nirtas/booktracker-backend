package ru.jerael.booktracker.backend.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.jerael.booktracker.backend.domain.model.author.Author;
import ru.jerael.booktracker.backend.domain.model.pagination.PageQuery;
import ru.jerael.booktracker.backend.domain.model.pagination.PageResult;
import ru.jerael.booktracker.backend.domain.usecase.author.SearchAuthorsUseCase;
import ru.jerael.booktracker.backend.web.dto.author.AuthorResponse;
import ru.jerael.booktracker.backend.web.mapper.AuthorWebMapper;
import java.util.List;

@Tag(name = "Authors")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/authors")
public class AuthorController {
    private final SearchAuthorsUseCase searchAuthorsUseCase;
    private final AuthorWebMapper authorWebMapper;

    @Operation(description = "Get author suggestions")
    @GetMapping("/autocomplete")
    public List<AuthorResponse> autocomplete(@RequestParam String query, @RequestParam int limit) {
        PageQuery pageQuery = new PageQuery(0, limit, null, null);
        PageResult<Author> result = searchAuthorsUseCase.execute(pageQuery, query);
        return authorWebMapper.toResponses(result);
    }
}
