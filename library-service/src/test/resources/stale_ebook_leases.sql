insert into books (id, title, publisher, edition, cover_page_image_asset_id) values ('d4c608c4-7ac6-48b9-b90d-9952e798578b', 'Lady Vengeance (Sympathy for Lady Vengeance) (Chinjeolhan geumjassi)', 'Wanda Sykes: Sick and Tired', 'Taboo (Gohatto)', '158NhLveCgNHGAaPjbz1E8AJD5X1SBy2HE');
insert into books (id, title, publisher, edition, cover_page_image_asset_id) values ('1651bee3-4a18-412a-89bc-abb9dcdfe203', 'Amazing Johnathan: Wrong on Every Level', 'Cure, The', 'We the Party', '12Jc5gSRXy9SPrKCukXFCEeByaSSLHVpHJ');
insert into books (id, title, publisher, edition, cover_page_image_asset_id) values ('7a900746-e6b7-4ddd-a3b5-99f4ad291321', 'Guard, The', 'That''s Entertainment! III', 'Bangkok Dangerous', '1vbuWupWzsiXN6eZ7PwBrsJXi2YpiTzT2');
insert into books (id, title, publisher, edition, cover_page_image_asset_id) values ('60dc01ad-43be-4501-aaa8-3c12741dff4f', 'War', 'The Boy', 'American Ninja 5', '18YBxX6ZcfwJMDM7r1P1cYHbqJ1L4FpMpa');
insert into books (id, title, publisher, edition, cover_page_image_asset_id) values ('2e611bfb-d06d-4ef9-9bf6-8d4f445ba736', 'Play Girl', 'Hong Kong Confidential (Amaya)', 'Hatchet', '15bFAeZ4oesWsgVE5cTqSqQxpQDHxmyezA');

insert into physical_books (book_id, copies_available, fine_amount, fine_currency_code) values ('d4c608c4-7ac6-48b9-b90d-9952e798578b', 5, 44.25, 'JPY');
insert into physical_books (book_id, copies_available, fine_amount, fine_currency_code) values ('1651bee3-4a18-412a-89bc-abb9dcdfe203', 10, 67.45, 'CNY');
insert into physical_books (book_id, copies_available, fine_amount, fine_currency_code) values ('7a900746-e6b7-4ddd-a3b5-99f4ad291321', 9, 72.65, 'CNY');

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