package com.github.sujankumarmitra.libraryservice.v1.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Set;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

/**
 * @author skmitra
 * @since Dec 03/12/21, 2021
 */
@JsonTypeInfo(use = NAME, property = "type")
@JsonSubTypes({
        @Type(name = "PHYSICAL_BOOK", value = JacksonValidUpdatePhysicalBookRequest.class),
        @Type(name = "EBOOK", value = JacksonValidUpdateEBookRequest.class)
})
@Getter
@Setter
public abstract class JacksonValidUpdateBookRequest {

    @JsonIgnore
    private String id;
    @NotEmpty
    private String libraryId;
    @Size(min = 1)
    private String title;
    @Size(min = 1)
    private String publisher;
    @Size(min = 1)
    private String edition;
    @Size(min = 1)
    private String coverPageImageAssetId;
    @Size(min = 1)
    private Set<JacksonValidUpdateBookAuthorRequest> authors;
    private Set<JacksonValidUpdateBookTagRequest> tags;

    public abstract JacksonBookType getType();
}
