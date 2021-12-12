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
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.UUID;

/**
 * @author skmitra
 * @since Nov 16/11/21, 2021
 */
@Repository
@AllArgsConstructor
public class R2dbcPostgresqlAssetDao implements AssetDao {

    public static final String INSERT_STATEMENT = "INSERT INTO assets(name, owner_id, access_level) VALUES ($1, $2, $3) RETURNING id";
    public static final String SELECT_STATEMENT = "SELECT id, name, owner_id, access_level FROM assets WHERE id=$1";
    public static final String DELETE_STATEMENT = "DELETE FROM assets WHERE id=$1";
    private final ConnectionAccessor connectionAccessor;

    @Override
    public Mono<Asset> insert(Asset asset) {
        return connectionAccessor.inConnection(conn ->
                        Mono.from(conn
                                .createStatement(INSERT_STATEMENT)
                                .bind("$1", asset.getName())
                                .bind("$2", asset.getOwnerId())
                                .bind("$3", asset.getAccessLevel().toString())
                                .execute()))
                .flatMap(result ->
                        Mono.from(result.map((row, rowMetadata) -> row.get("id", UUID.class))))
                .map(Object::toString)
                .map(assetId -> new DefaultAsset(assetId, asset.getName(), asset.getOwnerId(), asset.getAccessLevel()));
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
                        Mono.from(conn.createStatement(SELECT_STATEMENT)
                                .bind("$1", UUID.fromString(assetId))
                                .execute()))
                .flatMap(result -> Mono.from(result.map(this::mapToAsset)))
                .onErrorResume(IllegalArgumentException.class, th -> Mono.empty());
    }

    private Asset mapToAsset(Row row, RowMetadata rowMetadata) {
        return DefaultAsset
                .builder()
                .id(Objects.requireNonNull(row.get("id", UUID.class)).toString())
                .name(Objects.requireNonNull(row.get("name", String.class)))
                .ownerId(Objects.requireNonNull(row.get("owner_id", String.class)))
                .accessLevel(AccessLevel.valueOf(row.get("access_level", String.class)))
                .build();
    }
}
