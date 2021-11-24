CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS books(
	id uuid DEFAULT uuid_generate_v4(),
	title text,
	publisher text,
	edition text,
	cover_page_image_id text,
	CONSTRAINT pk_books PRIMARY KEY (id),
	CONSTRAINT chk_books_title_not_null CHECK (title IS NOT NULL),
	CONSTRAINT chk_books_title_not_empty CHECK (LENGTH(title) > 0),
	CONSTRAINT chk_books_publisher_not_null CHECK (publisher IS NOT NULL),
	CONSTRAINT chk_books_publisher_not_empty CHECK (LENGTH(publisher) > 0),
	CONSTRAINT chk_books_edition_not_null CHECK (edition IS NOT NULL),
	CONSTRAINT chk_books_edition_not_empty CHECK (LENGTH(edition) > 0)
);

CREATE TABLE IF NOT EXISTS authors(
	book_id uuid,
	name text,
	CONSTRAINT pk_authors PRIMARY KEY(book_id,name),
	CONSTRAINT fk_authors_books FOREIGN KEY(book_id) REFERENCES books(id),
	CONSTRAINT chk_authors_name_not_null CHECK(name IS NOT NULL),
	CONSTRAINT chk_authors_name_not_empty CHECK(LENGTH(name) > 0)
);

CREATE TABLE IF NOT EXISTS tags(
	book_id uuid,
	key varchar(255),
	value text,
	CONSTRAINT pk_tags PRIMARY KEY(book_id, key),
	CONSTRAINT fk_tags_books FOREIGN KEY(book_id) REFERENCES books(id),
	CONSTRAINT chk_tags_key_not_null CHECK(key IS NOT NULL),
	CONSTRAINT chk_tags_key_not_empty CHECK(LENGTH(key) > 0),
	CONSTRAINT chk_tags_value_not_null CHECK(value IS NOT NULL),
	CONSTRAINT chk_tags_value_not_empty CHECK(LENGTH(value) > 0)
);

CREATE TABLE IF NOT EXISTS physical_books(
	book_id uuid,
	copies_available bigint DEFAULT 0,
	fine_amount numeric DEFAULT 0.0,
	currency_code char(3),
	CONSTRAINT pk_physical_books PRIMARY KEY(book_id),
	CONSTRAINT fk_physical_books_books FOREIGN KEY(book_id) REFERENCES books(id),
	CONSTRAINT chk_physical_book_copies_not_null CHECK(copies_available IS NOT NULL),
	CONSTRAINT chk_physical_book_copies_positive CHECK(copies_available >= 0),
	CONSTRAINT chk_physical_fine_amount_not_null CHECK(fine_amount IS NOT NULL),
	CONSTRAINT chk_physical_book_fine_amount_positive CHECK(fine_amount >= 0.0),
	CONSTRAINT chk_physical_book_currency_code_not_null CHECK(currency_code IS NOT NULL),
	CONSTRAINT chk_physical_book_currency_code_valid CHECK(LENGTH(currency_code) = 3)
);

CREATE TABLE IF NOT EXISTS e_book_segments(
	id text,
	book_id uuid,
	start_page bigint,
	end_page bigint,
	CONSTRAINT pk_e_book_segments PRIMARY KEY(id),
	CONSTRAINT fk_e_book_segments_books FOREIGN KEY(book_id) REFERENCES books(id),
	CONSTRAINT chk_e_book_book_id_not_null CHECK(book_id IS NOT NULL),
	CONSTRAINT chk_e_book_start_page_not_null CHECK(start_page IS NOT NULL),
	CONSTRAINT chk_e_book_start_page_positive CHECK(start_page >= 0),
	CONSTRAINT chk_e_book_end_page_not_null CHECK(end_page IS NOT NULL),
	CONSTRAINT chk_e_book_end_page_positive CHECK(end_page >= 0)
);

CREATE TABLE IF NOT EXISTS packages(
	id uuid DEFAULT uuid_generate_v4(),
	name text,
	CONSTRAINT pk_packages PRIMARY KEY(id),
	CONSTRAINT chk_packages_name_not_null CHECK(name IS NOT NULL),
	CONSTRAINT chk_packages_name_not_empty CHECK(LENGTH(name) > 0)
);

CREATE TABLE IF NOT EXISTS package_items(
	package_id uuid,
	book_id uuid,
	CONSTRAINT pk_package_items PRIMARY KEY (package_id,book_id),
	CONSTRAINT fk_package_items_book_packages FOREIGN KEY(package_id) REFERENCES packages(id),
	CONSTRAINT fk_package_items_books FOREIGN KEY(book_id) REFERENCES books(id),
	CONSTRAINT chk_package_items_package_id_not_null CHECK(package_id IS NOT NULL),
	CONSTRAINT chk_package_items_book_id_not_null CHECK(book_id IS NOT NULL)
);

CREATE TABLE IF NOT EXISTS lease_requests (
	id uuid DEFAULT uuid_generate_v4(),
	book_id uuid,
	user_id varchar(255),
	timestamp bigint DEFAULT extract(epoch from now()),
	status varchar(255),
	CONSTRAINT pk_lease_requests PRIMARY KEY(id),
 	CONSTRAINT fk_lease_requests_books FOREIGN KEY(book_id) REFERENCES books(id),
	CONSTRAINT uq_lease_requests_book_id_user_id UNIQUE (book_id,user_id),
	CONSTRAINT chk_lease_requests_book_id_not_null CHECK(book_id IS NOT NULL),
	CONSTRAINT chk_lease_requests_user_id_not_null CHECK(user_id IS NOT NULL),
	CONSTRAINT chk_lease_requests_user_id_not_empty CHECK(LENGTH(user_id) > 0),
	CONSTRAINT chk_lease_requests_timestamp_not_null CHECK(timestamp IS NOT NULL),
	CONSTRAINT chk_lease_requests_timestamp_positive CHECK(timestamp >= 0),
	CONSTRAINT chk_lease_requests_status_not_null CHECK(status IS NOT NULL),
	CONSTRAINT chk_lease_requests_status_valid CHECK(status IN ('PENDING', 'ACCEPTED', 'REJECTED'))
);

CREATE TABLE IF NOT EXISTS lease_records(
	lease_request_id uuid,
	start_time bigint DEFAULT extract(epoch from now()),
	end_time bigint,
	relinquished boolean DEFAULT FALSE,
	CONSTRAINT pk_lease_records PRIMARY KEY(lease_request_id),
	CONSTRAINT fk_lease_records_lease_requests FOREIGN KEY(lease_request_id) REFERENCES lease_requests(id),
	CONSTRAINT chk_lease_records_start_time_not_null CHECK(start_time IS NOT NULL),
	CONSTRAINT chk_lease_records_start_time_positive CHECK(start_time >= 0),
	CONSTRAINT chk_lease_records_end_time_positive CHECK(end_time >= 0),
	CONSTRAINT chk_lease_records_relinquished_not_null CHECK(relinquished IS NOT NULL)
);