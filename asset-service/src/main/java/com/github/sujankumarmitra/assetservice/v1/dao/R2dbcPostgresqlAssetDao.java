package com.github.sujankumarmitra.assetservice.v1.dao;

import com.github.sujankumarmitra.assetservice.v1.model.AccessLevel;
import com.github.sujankumarmitra.assetservice.v1.model.Asset;
import com.github.sujankumarmitra.assetservice.v1.model.DefaultAsset;
import io.r2dbc.spi.Result;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import lombok.AllArgsConstructor;
import org.springframework.r2dbc.core.ConnectionAccessor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static java.util.Objects.requireNonNull;

/**
 * @author skmitra
 * @since Nov 16/11/21, 2021
 */
@Repository
@AllArgsConstructor
public class R2dbcPostgresqlAssetDao implements AssetDao {

    public static final String INSERT_STATEMENT = "INSERT INTO assets(name, library_id, mime_type, access_level) VALUES ($1, $2, $3, $4) RETURNING id";
    public static final String SELECT_BY_ID_STATEMENT = "SELECT id, name, library_id, mime_type, access_level FROM assets WHERE id=$1";
    public static final String SELECT_BY_LIBRARY_ID_STATEMENT = "SELECT id, name, library_id, mime_type, access_level FROM assets WHERE library_id=$1";
    public static final String DELETE_STATEMENT = "DELETE FROM assets WHERE id=$1";
    private final ConnectionAccessor connectionAccessor;

    @Override
    public Mono<Asset> insert(Asset asset) {
        return connectionAccessor.inConnection(conn ->
                        Mono.from(conn
                                .createStatement(INSERT_STATEMENT)
                                .bind("$1", asset.getName())
                                .bind("$2", asset.getLibraryId())
                                .bind("$3", asset.getMimeType())
                                .bind("$4", asset.getAccessLevel().toString())
                                .execute()))
                .flatMap(result ->
                        Mono.from(result.map((row, rowMetadata) -> row.get("id", UUID.class))))
                .map(Object::toString)
                .map(assetId -> new DefaultAsset(assetId, asset.getName(), asset.getLibraryId(), asset.getMimeType(), asset.getAccessLevel()));
    }

    @Override
    public Mono<Void> delete(String assetId) {
        return connectionAccessor.inConnection(conn ->
                        Mono.from(conn
                                .createStatement(DELETE_STATEMENT)
                                .bind("$1", UUID.fromString(assetId))
                                .execute()))
                .flatMapMany(Result::getRowsUpdated)
                .then()
                .onErrorResume(IllegalArgumentException.class, th -> Mono.empty());
    }

    @Override
    public Mono<Asset> findOne(String assetId) {
        return connectionAccessor.inConnection(conn ->
                        Mono.from(conn.createStatement(SELECT_BY_ID_STATEMENT)
                                .bind("$1", UUID.fromString(assetId))
                                .execute()))
                .flatMap(result -> Mono.from(result.map(this::mapToAsset)))
                .onErrorResume(IllegalArgumentException.class, th -> Mono.empty());
    }

    @Override
    public Flux<Asset> findByLibraryId(String libraryId) {
        return connectionAccessor
                .inConnectionMany(conn -> Flux
                        .from(conn.createStatement(SELECT_BY_LIBRARY_ID_STATEMENT)
                        .bind("$1", libraryId)
                        .execute()))
                .concatMap(result -> Mono.from(result.map(this::mapToAsset)));
    }

    private Asset mapToAsset(Row row, RowMetadata rowMetadata) {
        return DefaultAsset
                .builder()
                .id(requireNonNull(row.get("id", UUID.class)).toString())
                .name(requireNonNull(row.get("name", String.class)))
                .libraryId(requireNonNull(row.get("library_id", String.class)))
                .mimeType(requireNonNull(row.get("mime_type", String.class)))
                .accessLevel(AccessLevel.valueOf(row.get("access_level", String.class)))
                .build();
    }
}
