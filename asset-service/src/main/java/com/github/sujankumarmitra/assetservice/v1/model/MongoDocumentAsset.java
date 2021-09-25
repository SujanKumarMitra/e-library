package com.github.sujankumarmitra.assetservice.v1.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

/**
 * @author skmitra
 * @since Sep 25/09/21, 2021
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "assets")
public class MongoDocumentAsset implements Asset {
    @Id
    private String id;
    private String name;
}