CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS books(
	id uuid DEFAULT uuid_generate_v4(),
	title text,
	publisher text,
	edition text,
	cover_page_image_asset_id text,
	CONSTRAINT pk_books PRIMARY KEY (id),
	CONSTRAINT chk_books_title_not_null CHECK (title IS NOT NULL),
	CONSTRAINT chk_books_title_not_empty CHECK (LENGTH(title) > 0),
	CONSTRAINT chk_books_publisher_not_null CHECK (publisher IS NOT NULL),
	CONSTRAINT chk_books_publisher_not_empty CHECK (LENGTH(publisher) > 0),
	CONSTRAINT chk_books_edition_not_null CHECK (edition IS NOT NULL),
	CONSTRAINT chk_books_edition_not_empty CHECK (LENGTH(edition) > 0)
);

CREATE TABLE IF NOT EXISTS authors(
    id uuid DEFAULT uuid_generate_v4(),
	book_id uuid,
	name text,
	CONSTRAINT pk_authors PRIMARY KEY(id),
	CONSTRAINT fk_authors_books FOREIGN KEY(book_id) REFERENCES books(id),
	CONSTRAINT unq_authors_book_id_name UNIQUE(book_id, name),
	CONSTRAINT chk_authors_name_not_null CHECK(name IS NOT NULL),
	CONSTRAINT chk_authors_name_not_empty CHECK(LENGTH(name) > 0)
);

CREATE TABLE IF NOT EXISTS book_tags(
    id uuid DEFAULT uuid_generate_v4(),
	book_id uuid,
	key text,
	value text,
	CONSTRAINT pk_book_tags PRIMARY KEY(id),
	CONSTRAINT fk_book_tags_books FOREIGN KEY(book_id) REFERENCES books(id),
	CONSTRAINT unq_book_tags_book_id_key UNIQUE(book_id,key),
	CONSTRAINT chk_book_tags_key_not_null CHECK(key IS NOT NULL),
	CONSTRAINT chk_book_tags_key_not_empty CHECK(LENGTH(key) > 0),
	CONSTRAINT chk_book_tags_value_not_null CHECK(value IS NOT NULL),
	CONSTRAINT chk_book_tags_value_not_empty CHECK(LENGTH(value) > 0)
);

CREATE TABLE IF NOT EXISTS physical_books(
	book_id uuid,
	copies_available bigint DEFAULT 0,
	fine_amount numeric DEFAULT 0.0,
	fine_currency_code char(3),
	CONSTRAINT pk_physical_books PRIMARY KEY(book_id),
	CONSTRAINT fk_physical_books_books FOREIGN KEY(book_id) REFERENCES books(id),
	CONSTRAINT chk_physical_book_copies_not_null CHECK(copies_available IS NOT NULL),
	CONSTRAINT chk_physical_book_copies_positive CHECK(copies_available >= 0),
	CONSTRAINT chk_physical_fine_amount_not_null CHECK(fine_amount IS NOT NULL),
	CONSTRAINT chk_physical_book_fine_amount_positive CHECK(fine_amount >= 0.0),
	CONSTRAINT chk_physical_book_fine_currency_code_not_null CHECK(fine_currency_code IS NOT NULL),
	CONSTRAINT chk_physical_book_fine_currency_code_valid CHECK(LENGTH(fine_currency_code) = 3)
);

CREATE TABLE IF NOT EXISTS ebooks(
	book_id uuid,
	format text,
	CONSTRAINT pk_ebooks PRIMARY KEY(book_id),
	CONSTRAINT fk_ebooks_books FOREIGN KEY(book_id) REFERENCES books(id),
	CONSTRAINT chk_ebook_format_not_null CHECK(format IS NOT NULL),
	CONSTRAINT chk_ebook_format_valid CHECK(format IN ('PDF'))
);

CREATE TABLE IF NOT EXISTS ebook_segments(
	id text,
	book_id uuid,
	index bigint,
	asset_id text,
	CONSTRAINT pk_ebook_segments PRIMARY KEY(id),
	CONSTRAINT fk_ebook_segments_books FOREIGN KEY(book_id) REFERENCES ebooks(book_id),
	CONSTRAINT chk_ebook_book_id_not_null CHECK(book_id IS NOT NULL),
	CONSTRAINT chk_ebook_index_not_null CHECK(index IS NOT NULL),
	CONSTRAINT chk_ebook_index_positive CHECK(index >= 0),
	CONSTRAINT chk_ebook_asset_id_not_null CHECK(asset_id IS NOT NULL)
);

CREATE TABLE IF NOT EXISTS packages(
	id uuid DEFAULT uuid_generate_v4(),
	name text,
	CONSTRAINT pk_packages PRIMARY KEY(id),
	CONSTRAINT chk_packages_name_not_null CHECK(name IS NOT NULL),
	CONSTRAINT chk_packages_name_not_empty CHECK(LENGTH(name) > 0)
);

CREATE TABLE IF NOT EXISTS package_tags(
    id uuid DEFAULT uuid_generate_v4(),
	package_id uuid,
	key text,
	value text,
	CONSTRAINT pk_package_tags PRIMARY KEY(id),
	CONSTRAINT fk_package_tags_packages FOREIGN KEY(package_id) REFERENCES packages(id),
	CONSTRAINT unq_package_tags_book_id_key UNIQUE(package_id,key),
	CONSTRAINT chk_package_tags_key_not_null CHECK(key IS NOT NULL),
	CONSTRAINT chk_package_tags_key_not_empty CHECK(LENGTH(key) > 0),
	CONSTRAINT chk_package_tags_value_not_null CHECK(value IS NOT NULL),
	CONSTRAINT chk_package_tags_value_not_empty CHECK(LENGTH(value) > 0)
);

CREATE TABLE IF NOT EXISTS package_items(
    id uuid DEFAULT uuid_generate_v4(),
	package_id uuid,
	book_id uuid,
	CONSTRAINT pk_package_items PRIMARY KEY (id),
	CONSTRAINT fk_package_items_packages FOREIGN KEY(package_id) REFERENCES packages(id),
	CONSTRAINT fk_package_items_books FOREIGN KEY(book_id) REFERENCES books(id),
	CONSTRAINT unq_package_items_package_id_book_id UNIQUE (package_id,book_id),
	CONSTRAINT chk_package_items_package_id_not_null CHECK(package_id IS NOT NULL),
	CONSTRAINT chk_package_items_book_id_not_null CHECK(book_id IS NOT NULL)
);

CREATE TABLE IF NOT EXISTS lease_requests (
	id uuid DEFAULT uuid_generate_v4(),
	book_id uuid,
	user_id text,
	timestamp bigint DEFAULT extract(epoch from now()),
	status text DEFAULT 'PENDING',
	CONSTRAINT pk_lease_requests PRIMARY KEY(id),
 	CONSTRAINT fk_lease_requests_books FOREIGN KEY(book_id) REFERENCES books(id),
	CONSTRAINT chk_lease_requests_book_id_not_null CHECK(book_id IS NOT NULL),
	CONSTRAINT chk_lease_requests_user_id_not_null CHECK(user_id IS NOT NULL),
	CONSTRAINT chk_lease_requests_user_id_not_empty CHECK(LENGTH(user_id) > 0),
	CONSTRAINT chk_lease_requests_timestamp_not_null CHECK(timestamp IS NOT NULL),
	CONSTRAINT chk_lease_requests_timestamp_positive CHECK(timestamp >= 0),
	CONSTRAINT chk_lease_requests_status_not_null CHECK(status IS NOT NULL),
	CONSTRAINT chk_lease_requests_status_valid CHECK(status IN ('PENDING', 'ACCEPTED', 'REJECTED', 'EXPIRED'))
);

CREATE TABLE IF NOT EXISTS accepted_lease_requests(
	lease_request_id uuid,
	start_time bigint DEFAULT extract(epoch from now()),
	duration bigint,
	relinquished boolean DEFAULT FALSE,
	CONSTRAINT pk_accepted_lease_requests PRIMARY KEY(lease_request_id),
	CONSTRAINT fk_accepted_lease_requests_lease_requests FOREIGN KEY(lease_request_id) REFERENCES lease_requests(id),
	CONSTRAINT chk_accepted_lease_requests_start_time_not_null CHECK(start_time IS NOT NULL),
	CONSTRAINT chk_accepted_lease_requests_start_time_positive CHECK(start_time >= 0),
	CONSTRAINT chk_accepted_lease_requests_duration_not_null CHECK(duration IS NOT NULL),
	CONSTRAINT chk_accepted_lease_requests_duration_valid CHECK((duration = -1 OR duration > 0)),
	CONSTRAINT chk_accepted_lease_requests_relinquished_not_null CHECK(relinquished IS NOT NULL)
);

CREATE TABLE IF NOT EXISTS rejected_lease_requests(
	lease_request_id uuid,
	reason_phrase text,
	CONSTRAINT pk_rejected_lease_requests PRIMARY KEY(lease_request_id),
	CONSTRAINT fk_rejected_lease_requests_lease_requests FOREIGN KEY(lease_request_id) REFERENCES lease_requests(id),
	CONSTRAINT chk_rejected_lease_requests_reason_phrase_not_null CHECK(reason_phrase IS NOT NULL),
	CONSTRAINT chk_rejected_lease_requests_reason_phrase_not_empty CHECK(LENGTH(reason_phrase) > 0)
);
