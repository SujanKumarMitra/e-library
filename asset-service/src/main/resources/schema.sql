CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS assets(
    id uuid DEFAULT uuid_generate_v4(),
    name text,
    library_id text,
    mime_type text,
    access_level text,
    CONSTRAINT pk_assets PRIMARY KEY (id),
    CONSTRAINT chk_assets_access_level CHECK(access_level IN ('PUBLIC', 'PRIVATE'))
);

CREATE TABLE IF NOT EXISTS asset_permissions(
    asset_id uuid,
    subject_id text,
    grant_start bigint,
    grant_duration bigint,
    CONSTRAINT asset_permissions_pk PRIMARY KEY (asset_id, subject_id),
    CONSTRAINT asset_permissions_fk FOREIGN KEY (asset_id) REFERENCES assets(id) ON DELETE CASCADE
);