#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "asset_service" --dbname "asset_service_db" --file=/docker-entrypoint-initdb.d/scripts/asset_service_db.sql

psql -v ON_ERROR_STOP=1 --username "library_service" --dbname "library_service_db" --file=/docker-entrypoint-initdb.d/scripts/library_service_db.sql