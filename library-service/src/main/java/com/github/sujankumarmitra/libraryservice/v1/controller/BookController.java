package com.github.sujankumarmitra.libraryservice.v1.controller;

import com.github.sujankumarmitra.libraryservice.v1.config.OpenApiConfiguration.ApiAcceptedResponse;
import com.github.sujankumarmitra.libraryservice.v1.config.OpenApiConfiguration.ApiBadRequestResponse;
import com.github.sujankumarmitra.libraryservice.v1.controller.dto.*;
import com.github.sujankumarmitra.libraryservice.v1.controller.dto.JacksonValidCreateEBookRequestAdaptor;
import com.github.sujankumarmitra.libraryservice.v1.controller.dto.JacksonValidCreatePhysicalBookRequestAdaptor;
import com.github.sujankumarmitra.libraryservice.v1.controller.dto.JacksonValidUpdateEBookRequestAdaptor;
import com.github.sujankumarmitra.libraryservice.v1.controller.dto.JacksonValidUpdatePhysicalBookRequestAdaptor;
import com.github.sujankumarmitra.libraryservice.v1.model.Book;
import com.github.sujankumarmitra.libraryservice.v1.model.EBook;
import com.github.sujankumarmitra.libraryservice.v1.model.PhysicalBook;
import com.github.sujankumarmitra.libraryservice.v1.openapi.schema.*;
import com.github.sujankumarmitra.libraryservice.v1.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;

import static com.github.sujankumarmitra.libraryservice.v1.controller.dto.JacksonBookType.EBOOK;
import static com.github.sujankumarmitra.libraryservice.v1.controller.dto.JacksonBookType.PHYSICAL;

/**
 * @author skmitra
 * @since Nov 30/11/21, 2021
 */
@RestController
@RequestMapping(path = "/api/v1/books")
@AllArgsConstructor
@Tag(
        name = "BookController",
        description = "### Controller for managing books"
)
public class BookController {

    @NotNull
    private final BookService bookService;

    @Operation(
            summary = "Fetch all books",
            description = "Librarians/Teachers/Students will invoke this API." +
                    "<br> Please refer to the response schema to see how book type is determined",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(
                                            schema = @Schema(
                                                    oneOf = {GetPhysicalBookResponseSchema.class, GetEBookResponseSchema.class}
                                            )
                                    )
                            )
                    )
            }
    )
    @GetMapping
    public Flux<Book> getBooks(@RequestParam(value = "page_no", defaultValue = "0") long pageNo) {
        return Flux.empty();
    }


    @Operation(
            summary = "Search books by title and author",
            description = "Librarians/Teachers/Students will invoke this API." +
                    "<br> Please refer to the response schema to see how book type is determined",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(
                                            schema = @Schema(
                                                    oneOf = {GetPhysicalBookResponseSchema.class, GetEBookResponseSchema.class}
                                            )
                                    )
                            )
                    )
            }
    )
    @GetMapping("/search")
    public Flux<Book> getBooksByTitle(
            @RequestParam(required = false) String title,
            @RequestParam(value = "page_no", defaultValue = "0") long pageNo) {
        return Flux.empty();
    }


    @Operation(
            summary = "Create a book",
            description = "Librarians will invoke this API." +
                    "<br> Please refer to the request schema for separate book type",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            schema = @Schema(
                                    oneOf = {CreatePhysicalBookRequestSchema.class, CreateEBookRequestSchema.class}
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            headers = @Header(
                                    name = "Location",
                                    description = "Unique ID pointing to this book",
                                    schema = @Schema(
                                            example = "7d553b6b-c6e4-42a7-bc8d-7cda07909b2f"
                                    )
                            )
                    )
            }
    )
    @ApiBadRequestResponse
    @ApiAcceptedResponse
    @PostMapping
    public Mono<ResponseEntity<Void>> createBook(@RequestBody @Valid JacksonValidCreateBookRequest request) {
        JacksonBookType type = request.getType();

        Mono<String> createdBookId;

        if (type == PHYSICAL) {
            PhysicalBook book = new JacksonValidCreatePhysicalBookRequestAdaptor((JacksonValidCreatePhysicalBookRequest) request);
            createdBookId = bookService.createBook(book);
        } else if (type == EBOOK) {
            EBook book = new JacksonValidCreateEBookRequestAdaptor((JacksonValidCreateEBookRequest) request);
            createdBookId = bookService.createBook(book);
        } else {
            //this should not happen
            createdBookId = Mono.error(new IllegalArgumentException("BookType could not be determined"));
        }

        return createdBookId
                .map(id -> ResponseEntity.created(URI.create(id)).build());
    }

    @Operation(
            summary = "Update an existing book",
            description = "Librarians will invoke this API." +
                    "<br> Please refer to the request schema for separate book type"
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                    schema = @Schema(
                            oneOf = {UpdatePhysicalBookRequestSchema.class, UpdateEBookRequestSchema.class}
                    )
            )
    )
    @ApiResponse(responseCode = "202")
    @ApiAcceptedResponse
    @ApiBadRequestResponse
    @PatchMapping(path = "/{bookId}", consumes = {"application/merge-patch+json", "application/json"})
    public Mono<ResponseEntity<Void>> updateBook(@PathVariable("bookId") String bookId,
                                                 @RequestBody @Valid JacksonValidUpdateBookRequest request) {

        request.setId(bookId);
        if (request.getAuthors() != null)
            request.getAuthors().forEach(author -> author.setBookId(bookId));
        if (request.getTags() != null)
            request.getTags().forEach(tag -> tag.setBookId(bookId));

        JacksonBookType type = request.getType();

        Mono<Void> updateMono;

        if (type == PHYSICAL) {
            PhysicalBook book = new JacksonValidUpdatePhysicalBookRequestAdaptor((JacksonValidUpdatePhysicalBookRequest) request);
            updateMono = bookService.updateBook(book);
        } else if (type == EBOOK) {
            EBook book = new JacksonValidUpdateEBookRequestAdaptor((JacksonValidUpdateEBookRequest) request);
            updateMono = bookService.updateBook(book);
        } else {
            // should not happen
            updateMono = Mono.error(new IllegalArgumentException("could not determine bookType"));
        }

        return updateMono
                .thenReturn(ResponseEntity.accepted().build());
    }

    @Operation(
            summary = "Delete a book",
            description = "Librarians will invoke this API")
    @ApiAcceptedResponse
    @DeleteMapping("/{bookId}")
    public Mono<ResponseEntity<Void>> deleteBook(@PathVariable("bookId") String bookId) {
        return bookService.deleteBook(bookId)
                .thenReturn(ResponseEntity.accepted().build());
    }
}
