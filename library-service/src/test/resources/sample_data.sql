-- books
insert into books (library_id, id, title, publisher, edition, cover_page_image_asset_id) values ('library1', 'd4c608c4-7ac6-48b9-b90d-9952e798578b', 'Lady Vengeance (Sympathy for Lady Vengeance) (Chinjeolhan geumjassi)', 'Wanda Sykes: Sick and Tired', 'Taboo (Gohatto)', '158NhLveCgNHGAaPjbz1E8AJD5X1SBy2HE');
insert into books (library_id, id, title, publisher, edition, cover_page_image_asset_id) values ('library2', '1651bee3-4a18-412a-89bc-abb9dcdfe203', 'Amazing Johnathan: Wrong on Every Level', 'Cure, The', 'We the Party', '12Jc5gSRXy9SPrKCukXFCEeByaSSLHVpHJ');
insert into books (library_id, id, title, publisher, edition, cover_page_image_asset_id) values ('library1', '7a900746-e6b7-4ddd-a3b5-99f4ad291321', 'Guard, The', 'That''s Entertainment! III', 'Bangkok Dangerous', '1vbuWupWzsiXN6eZ7PwBrsJXi2YpiTzT2');
insert into books (library_id, id, title, publisher, edition, cover_page_image_asset_id) values ('library2', '60dc01ad-43be-4501-aaa8-3c12741dff4f', 'War', 'The Boy', 'American Ninja 5', '18YBxX6ZcfwJMDM7r1P1cYHbqJ1L4FpMpa');
insert into books (library_id, id, title, publisher, edition, cover_page_image_asset_id) values ('library3', '2e611bfb-d06d-4ef9-9bf6-8d4f445ba736', 'Play Girl', 'Hong Kong Confidential (Amaya)', 'Hatchet', '15bFAeZ4oesWsgVE5cTqSqQxpQDHxmyezA');

--packages
insert into packages (id, library_id, name) values ('d913745e-9328-49ca-94fa-2ca7118ae1d2', 'library1', 'Veribet');
insert into packages (id, library_id, name) values ('b4455113-452b-44af-9873-ac2bfef90129', 'library1', 'It');
insert into packages (id, library_id, name) values ('5b55d220-37f6-4f53-992a-4686d47e3e3f', 'library2', 'Sub-Ex');
insert into packages (id, library_id, name) values ('5b9040dd-7425-4c1d-b311-7231ceb18122', 'library2', 'Job');
insert into packages (id, library_id, name) values ('b1b59ce4-05cc-4732-92ea-34249e3264b7', 'library3', 'Asoka');

--package_items
insert into package_items (package_id, book_id) values('d913745e-9328-49ca-94fa-2ca7118ae1d2','d4c608c4-7ac6-48b9-b90d-9952e798578b');
insert into package_items (package_id, book_id) values('d913745e-9328-49ca-94fa-2ca7118ae1d2','1651bee3-4a18-412a-89bc-abb9dcdfe203');

--package_tags
insert into package_tags (id, package_id, key, value) values ('9fd08ff8-0494-45ef-a4cb-6c5cc80f18ff', 'd913745e-9328-49ca-94fa-2ca7118ae1d2', '261-qvd-543', '012-hjg-800');
insert into package_tags (id, package_id, key, value) values ('f70c08df-e7ad-4cc5-97d1-cbeb7e8797b0', 'd913745e-9328-49ca-94fa-2ca7118ae1d2', '846-yqh-009', '198-qiq-361');
insert into package_tags (id, package_id, key, value) values ('3d959410-c5c6-49b4-8e47-b2acdbadf7a7', 'b4455113-452b-44af-9873-ac2bfef90129', '076-hnw-899', '244-emj-548');
insert into package_tags (id, package_id, key, value) values ('874ca89e-cb04-403d-a5e7-fcc1c273796f', 'b4455113-452b-44af-9873-ac2bfef90129', '965-bod-141', '699-xkj-304');
insert into package_tags (id, package_id, key, value) values ('5a6b693a-cf55-40fb-a492-56ac356cd614', 'b4455113-452b-44af-9873-ac2bfef90129', '787-fcv-893', '284-ilo-479');

--book_tags
insert into book_tags (id, book_id, key, value) values ('9fd08ff8-0494-45ef-a4cb-6c5cc80f18ff', 'd4c608c4-7ac6-48b9-b90d-9952e798578b', '261-qvd-543', '012-hjg-800');
insert into book_tags (id, book_id, key, value) values ('f70c08df-e7ad-4cc5-97d1-cbeb7e8797b0', 'd4c608c4-7ac6-48b9-b90d-9952e798578b', '846-yqh-009', '198-qiq-361');
insert into book_tags (id, book_id, key, value) values ('3d959410-c5c6-49b4-8e47-b2acdbadf7a7', '1651bee3-4a18-412a-89bc-abb9dcdfe203', '076-hnw-899', '244-emj-548');
insert into book_tags (id, book_id, key, value) values ('874ca89e-cb04-403d-a5e7-fcc1c273796f', '1651bee3-4a18-412a-89bc-abb9dcdfe203', '965-bod-141', '699-xkj-304');
insert into book_tags (id, book_id, key, value) values ('5a6b693a-cf55-40fb-a492-56ac356cd614', '1651bee3-4a18-412a-89bc-abb9dcdfe203', '787-fcv-893', '284-ilo-479');

--authors
insert into authors(book_id, name) values ('d4c608c4-7ac6-48b9-b90d-9952e798578b', 'Aimbo');
insert into authors(book_id, name) values ('d4c608c4-7ac6-48b9-b90d-9952e798578b', 'Skipstorm');
insert into authors(book_id, name) values ('1651bee3-4a18-412a-89bc-abb9dcdfe203', 'Demizz');
insert into authors(book_id, name) values ('7a900746-e6b7-4ddd-a3b5-99f4ad291321', 'Jabberbean');
insert into authors(book_id, name) values ('2e611bfb-d06d-4ef9-9bf6-8d4f445ba736', 'Fatz');

-- physical books
insert into physical_books (book_id, copies_available, fine_amount, fine_currency_code) values ('d4c608c4-7ac6-48b9-b90d-9952e798578b', 5, 44.25, 'JPY');
insert into physical_books (book_id, copies_available, fine_amount, fine_currency_code) values ('1651bee3-4a18-412a-89bc-abb9dcdfe203', 10, 67.45, 'CNY');
insert into physical_books (book_id, copies_available, fine_amount, fine_currency_code) values ('7a900746-e6b7-4ddd-a3b5-99f4ad291321', 9, 72.65, 'CNY');

--ebooks
insert into ebooks (book_id, format) values ('60dc01ad-43be-4501-aaa8-3c12741dff4f', 'PDF');
insert into ebooks (book_id, format) values ('2e611bfb-d06d-4ef9-9bf6-8d4f445ba736', 'PDF');

-- physical book leases
insert into lease_requests (id, book_id, user_id, timestamp, status) values ('f4ba906f-33d7-4a1a-8fef-9949f23c2190', 'd4c608c4-7ac6-48b9-b90d-9952e798578b', 'eyanele0', (extract(epoch from now())*1000)::bigint, 'ACCEPTED');
insert into lease_requests (id, book_id, user_id, timestamp, status) values ('83484068-ef13-4198-a696-efbf510a06c2', '1651bee3-4a18-412a-89bc-abb9dcdfe203', 'cohms1', (extract(epoch from now())*1000)::bigint, 'PENDING');
insert into lease_requests (id, book_id, user_id, timestamp, status) values ('1f9b4344-99d9-4cbf-bae1-2394d60706fa', '7a900746-e6b7-4ddd-a3b5-99f4ad291321', 'abudnik2', (extract(epoch from now())*1000)::bigint, 'EXPIRED');

--ebook leases
insert into lease_requests (id, book_id, user_id, timestamp, status) values ('80a7d4b8-bd4f-4a31-bdf3-d42ab372892a', '2e611bfb-d06d-4ef9-9bf6-8d4f445ba736', 'apourveer3', (extract(epoch from now())*1000)::bigint, 'ACCEPTED');
insert into lease_requests (id, book_id, user_id, timestamp, status) values ('7ccd1d51-61af-4602-ab76-814792da682b', '2e611bfb-d06d-4ef9-9bf6-8d4f445ba736', 'qbloxland4', (extract(epoch from now())*1000)::bigint, 'ACCEPTED');
insert into lease_requests (id, book_id, user_id, timestamp, status) values ('61c22813-7faa-4fb4-9aee-2ce132520d9e', '60dc01ad-43be-4501-aaa8-3c12741dff4f', 'qbloxland5', (extract(epoch from now())*1000)::bigint, 'ACCEPTED');
insert into lease_requests (id, book_id, user_id, timestamp, status) values ('b9d8ed6b-8bb4-43cd-a3f9-7fed8b8b1d8f', '60dc01ad-43be-4501-aaa8-3c12741dff4f', 'qbloxland4', (extract(epoch from now())*1000)::bigint, 'ACCEPTED');

--physical book accepted leases
insert into accepted_lease_requests (lease_request_id, start_time, duration, relinquished) values ('f4ba906f-33d7-4a1a-8fef-9949f23c2190', 1638416584755, 259200000, FALSE);
insert into accepted_lease_requests (lease_request_id, start_time, duration, relinquished) values ('1f9b4344-99d9-4cbf-bae1-2394d60706fa', 1638415584755, -1, TRUE);

--ebook accepted leases
insert into accepted_lease_requests (lease_request_id, start_time, duration, relinquished) values ('80a7d4b8-bd4f-4a31-bdf3-d42ab372892a', 1638415584755, -1, FALSE);
insert into accepted_lease_requests (lease_request_id, start_time, duration, relinquished) values ('7ccd1d51-61af-4602-ab76-814792da682b', 1638415584755, 259200000, TRUE);
-- stale ebook leases
insert into accepted_lease_requests (lease_request_id, start_time, duration, relinquished) values ('61c22813-7faa-4fb4-9aee-2ce132520d9e', 1638415584755, 259200000, FALSE);
insert into accepted_lease_requests (lease_request_id, start_time, duration, relinquished) values ('b9d8ed6b-8bb4-43cd-a3f9-7fed8b8b1d8f', 1638415584755, 259200000, FALSE);