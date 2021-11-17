package com.github.sujankumarmitra.assetservice.v1.dao;

import com.github.sujankumarmitra.assetservice.v1.model.AssetPermission;
import com.github.sujankumarmitra.assetservice.v1.model.DefaultAssetPermission;
import io.r2dbc.spi.Result;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import lombok.AllArgsConstructor;
import org.springframework.r2dbc.core.ConnectionAccessor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * @author skmitra
 * @since Nov 16/11/21, 2021
 */
@Repository
@AllArgsConstructor
public class R2dbcPostgresqlAssetPermissionDao implements AssetPermissionDao {

    public static final String UPSERT_STATEMENT = "INSERT INTO asset_permissions " +
            "(asset_id, subject_id, grant_start, grant_duration) " +
            "VALUES ($1,$2,$3,$4) " +
            "ON CONFLICT ON CONSTRAINT asset_permissions_pk " +
            "DO UPDATE SET grant_start=$3, grant_duration=$4";
    public static final String SELECT_STATEMENT = "SELECT asset_id,subject_id,grant_start,grant_duration FROM asset_permissions WHERE asset_id=$1 AND subject_id=$2";

    private final ConnectionAccessor connectionAccessor;

    @Override
    public Mono<Void> upsert(AssetPermission permission) {
        return connectionAccessor.inConnection(conn ->
                        Mono.from(conn.createStatement(UPSERT_STATEMENT)
                                .bind("$1", UUID.fromString(permission.getAssetId()))
                                .bind("$2", permission.getSubjectId())
                                .bind("$3", permission.getGrantStartEpochMilliseconds())
                                .bind("$4", permission.getGrantDurationInMilliseconds())
                                .execute()))
                .flatMapMany(Result::getRowsUpdated)
                .then();
    }

    @Override
    public Mono<AssetPermission> findOne(String assetId, String subjectId) {

        return connectionAccessor.inConnection(conn ->
                        Mono.from(conn.createStatement(SELECT_STATEMENT)
                                .bind("$1", UUID.fromString(assetId))
                                .bind("$2", subjectId)
                                .execute()))
                .flatMap(result -> Mono.from(result.map(this::mapToAssetPermission)))
                .onErrorResume(IllegalArgumentException.class, th -> Mono.empty());

    }

    private AssetPermission mapToAssetPermission(Row row, RowMetadata rowMetadata) {
        return DefaultAssetPermission
                .newBuilder()
                .assetId(row.get("asset_id", UUID.class).toString())
                .subjectId(row.get("subject_id", String.class))
                .grantStartEpochMilliseconds(row.get("grant_start", Long.class))
                .grantDurationInMilliseconds(row.get("grant_duration", Long.class))
                .build();
    }
}
