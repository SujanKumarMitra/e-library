package com.github.sujankumarmitra.libraryservice.v1.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Size;
import java.util.Set;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

/**
 * @author skmitra
 * @since Dec 03/12/21, 2021
 */
@JsonTypeInfo(use = NAME, property = "type")
@JsonSubTypes({
        @Type(name = "PHYSICAL_BOOK", value = JacksonUpdatePhysicalBookRequest.class),
        @Type(name = "EBOOK", value = JacksonUpdateEBookRequest.class)
})
@Getter
@Setter
public abstract class JacksonUpdateBookRequest {

    @JsonIgnore
    private String id;
    @Size(min = 1)
    private String title;
    @Size(min = 1)
    private String publisher;
    @Size(min = 1)
    private String edition;
    @Size(min = 1)
    private String coverPageImageAssetId;
    @Size(min = 1)
    private Set<JacksonUpdateAuthorRequest> authors;
    @Size(min = 1)
    private Set<JacksonUpdateBookTagRequest> tags;

    public abstract BookType getType();
}
