CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS assets(
    id uuid DEFAULT uuid_generate_v4(),
    name varchar(255),
    CONSTRAINT assets_pk PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS asset_permissions(
    asset_id uuid,
    subject_id varchar(255),
    grant_start bigint,
    grant_duration bigint,
    CONSTRAINT asset_permissions_pk PRIMARY KEY (asset_id, subject_id),
    CONSTRAINT asset_permissions_fk FOREIGN KEY (asset_id) REFERENCES assets(id) ON DELETE CASCADE
);