package com.github.sujankumarmitra.libraryservice.v1.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.github.sujankumarmitra.libraryservice.v1.model.Book;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

/**
 * @author skmitra
 * @since Dec 03/12/21, 2021
 */
@JsonTypeInfo(use = NAME, property = "type")
@JsonSubTypes({
        @Type(name = "PHYSICAL_BOOK", value = JacksonCreatePhysicalBookRequest.class),
        @Type(name = "EBOOK", value = JacksonCreateEBookRequest.class)
})
@Getter
@Setter
public abstract class JacksonCreateBookRequest extends Book {
    @JsonIgnore
    private String id;
    @NotEmpty
    private String title;
    @NotEmpty
    private String publisher;
    @NotEmpty
    private String edition;
    private String coverPageImageAssetId;
    @NotNull
    @Size(min = 1)
    private Set<JacksonCreateAuthorRequest> authors;
    private Set<JacksonCreateBookTagRequest> tags;

    public abstract BookType getType();
}
