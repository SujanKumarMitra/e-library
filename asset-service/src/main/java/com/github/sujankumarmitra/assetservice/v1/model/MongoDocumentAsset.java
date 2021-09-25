package com.github.sujankumarmitra.assetservice.v1.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author skmitra
 * @since Sep 25/09/21, 2021
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "assets")
public class MongoDocumentAsset extends AbstractAsset {
    @Id
    private String id;
    private String name;

    @Override
    public boolean equals(Object o) {
        if(id == null || name == null) return false;
        if(!(o instanceof Asset)) return false;

        return super.equals(o);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}