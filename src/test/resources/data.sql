/*TRUSTED 2*/
INSERT INTO `recipients` (`id`, `uuid`)
    VALUES (1, '39a1979a-33a2-40db-9574-3711261dbc98');
/*ADMIN*/
INSERT INTO `recipients` (`id`, `uuid`)
    VALUES (2, '550e8400-e29b-41d4-a716-446655440000');
/* RELATIVE */
INSERT INTO `recipients` (`id`, `uuid`)
    VALUES (3, '8eef2b34-f116-40ad-8d83-13d389b799c4');
/*RELATIVE 2*/
INSERT INTO `recipients` (`id`, `uuid`)
    VALUES (4, 'af893113-e325-4e90-8169-33d798e9e52a');
/*TRUSTED 3*/
INSERT INTO `recipients` (`id`, `uuid`)
    VALUES (5, 'b57c1427-70db-4614-bac6-ec4e890d7e27');
/*ADMIN MY*/
INSERT INTO `recipients` (`id`, `uuid`)
    VALUES (6, '1b7b630a-b331-433b-b5b5-26ab4b6c51b8');


INSERT INTO `channel_types` (`id`, `code`)
    VALUES (1, 'EMAIL');
INSERT INTO `channel_types` (`id`, `code`)
    VALUES (2, 'TELEGRAM');


INSERT INTO `channels` (`id`, `type_id`, `recipient_id`)
    VALUES (1, 2, 2);
INSERT INTO `channels` (`id`, `type_id`, `recipient_id`)
    VALUES (2, 1, 2);
INSERT INTO `channels` (`id`, `type_id`, `recipient_id`)
    VALUES (3, 2, 3);
INSERT INTO `channels` (`id`, `type_id`, `recipient_id`)
    VALUES (4, 1, 4);
INSERT INTO `channels` (`id`, `type_id`, `recipient_id`)
    VALUES (5, 2, 5);
INSERT INTO `channels` (`id`, `type_id`, `recipient_id`)
    VALUES (6, 1, 6);
INSERT INTO `channels` (`id`, `type_id`, `recipient_id`)
    VALUES (7, 2, 6);


INSERT INTO `channels_email_details` (`channel_id`, `email`)
    VALUES (2, "admin@example.com");
INSERT INTO `channels_email_details` (`channel_id`, `email`)
    VALUES (4, "relative2@example.com");
INSERT INTO `channels_email_details` (`channel_id`, `email`)
    VALUES (6, "mail@danshin.net");


INSERT INTO `channels_telegram_details` (`channel_id`, `chat_id`, `username`)
    VALUES (1, "tgadmin", "tgadmin");
INSERT INTO `channels_telegram_details` (`channel_id`, `chat_id`, `username`)
    VALUES (3, "tgrelative", "tgrelative");
INSERT INTO `channels_telegram_details` (`channel_id`, `chat_id`, `username`)
    VALUES (5, "tgtrusted3", "tgtrusted3");
INSERT INTO `channels_telegram_details` (`channel_id`, `chat_id`, `username`)
    VALUES (7, "1370345612", "mldanshin");


INSERT INTO `verification_email_tokens` (`id`, `recipient_id`, `email`, `token`, `expires_at`, `created_at`)
    VALUES (1, 3, "relative@example.com", "19d8915c-6fc6-486b-9571-b3f69e71dee8", "2126-08-14 12:00:00", "2026-08-14 12:00:00");
INSERT INTO `verification_email_tokens` (`id`, `recipient_id`, `email`, `token`, `expires_at`, `created_at`)
    VALUES (2, 4, "trusted2@example.com", "ggd8915c-6fc6-486b-9571-b3f69e71dee8", "2126-08-13 12:00:00", "2026-08-14 12:00:00");


INSERT INTO `genealogy_dates` (`id`, `recipient_id`, `send_time`, `timezone`, `days_before`, `days_after`)
    VALUES (1, 1, '12:30:40', "Europe/Moscow", 12, 10);
INSERT INTO `genealogy_dates` (`id`, `recipient_id`, `send_time`, `timezone`, `days_before`, `days_after`)
    VALUES (2, 2, '09:34:11', "Asia/Dubai", 1, 1);
INSERT INTO `genealogy_dates` (`id`, `recipient_id`, `send_time`, `timezone`, `days_before`, `days_after`)
    VALUES (3, 3, '23:12:01', "Europe/Moscow", 2, 3);
INSERT INTO `genealogy_dates` (`id`, `recipient_id`, `send_time`, `timezone`, `days_before`, `days_after`)
    VALUES (4, 5, '14:02:21', "Asia/Dubai", 5, 1);
INSERT INTO `genealogy_dates` (`id`, `recipient_id`, `send_time`, `timezone`, `days_before`, `days_after`)
    VALUES (5, 6, '13:36:00', "Asia/Dubai", 5, 5);


INSERT INTO `genealogy_dates_types` (`id`, `name`) VALUES (1, 'BIRTH');
INSERT INTO `genealogy_dates_types` (`id`, `name`) VALUES (2, 'DEATH');


INSERT INTO `genealogy_dates_persons` (`id`, `genealogy_date_id`, `person_id`, `type_id`) VALUES (1, 3, 2, 1);
INSERT INTO `genealogy_dates_persons` (`id`, `genealogy_date_id`, `person_id`, `type_id`) VALUES (2, 3, 18, 1);
INSERT INTO `genealogy_dates_persons` (`id`, `genealogy_date_id`, `person_id`, `type_id`) VALUES (3, 3, 15, 1);
INSERT INTO `genealogy_dates_persons` (`id`, `genealogy_date_id`, `person_id`, `type_id`) VALUES (4, 3, 8, 1);
INSERT INTO `genealogy_dates_persons` (`id`, `genealogy_date_id`, `person_id`, `type_id`) VALUES (5, 3, 17, 2);
INSERT INTO `genealogy_dates_persons` (`id`, `genealogy_date_id`, `person_id`, `type_id`) VALUES (6, 3, 1, 2);
INSERT INTO `genealogy_dates_persons` (`id`, `genealogy_date_id`, `person_id`, `type_id`) VALUES (7, 2, 18, 1);
INSERT INTO `genealogy_dates_persons` (`id`, `genealogy_date_id`, `person_id`, `type_id`) VALUES (8, 2, 15, 1);
INSERT INTO `genealogy_dates_persons` (`id`, `genealogy_date_id`, `person_id`, `type_id`) VALUES (9, 2, 8, 1);
INSERT INTO `genealogy_dates_persons` (`id`, `genealogy_date_id`, `person_id`, `type_id`) VALUES (10, 2, 13, 1);
INSERT INTO `genealogy_dates_persons` (`id`, `genealogy_date_id`, `person_id`, `type_id`) VALUES (11, 2, 16, 1);
INSERT INTO `genealogy_dates_persons` (`id`, `genealogy_date_id`, `person_id`, `type_id`) VALUES (12, 3, 2, 2);
INSERT INTO `genealogy_dates_persons` (`id`, `genealogy_date_id`, `person_id`, `type_id`) VALUES (13, 2, 2, 2);
INSERT INTO `genealogy_dates_persons` (`id`, `genealogy_date_id`, `person_id`, `type_id`) VALUES (14, 5, 1, 1);
INSERT INTO `genealogy_dates_persons` (`id`, `genealogy_date_id`, `person_id`, `type_id`) VALUES (15, 5, 1, 2);
INSERT INTO `genealogy_dates_persons` (`id`, `genealogy_date_id`, `person_id`, `type_id`) VALUES (16, 5, 2, 1);
INSERT INTO `genealogy_dates_persons` (`id`, `genealogy_date_id`, `person_id`, `type_id`) VALUES (17, 5, 2, 2);
INSERT INTO `genealogy_dates_persons` (`id`, `genealogy_date_id`, `person_id`, `type_id`) VALUES (18, 5, 3, 1);
INSERT INTO `genealogy_dates_persons` (`id`, `genealogy_date_id`, `person_id`, `type_id`) VALUES (19, 5, 3, 2);
INSERT INTO `genealogy_dates_persons` (`id`, `genealogy_date_id`, `person_id`, `type_id`) VALUES (20, 5, 4, 1);
INSERT INTO `genealogy_dates_persons` (`id`, `genealogy_date_id`, `person_id`, `type_id`) VALUES (21, 5, 4, 2);
INSERT INTO `genealogy_dates_persons` (`id`, `genealogy_date_id`, `person_id`, `type_id`) VALUES (22, 5, 5, 1);
INSERT INTO `genealogy_dates_persons` (`id`, `genealogy_date_id`, `person_id`, `type_id`) VALUES (23, 5, 5, 2);
INSERT INTO `genealogy_dates_persons` (`id`, `genealogy_date_id`, `person_id`, `type_id`) VALUES (24, 5, 6, 1);
INSERT INTO `genealogy_dates_persons` (`id`, `genealogy_date_id`, `person_id`, `type_id`) VALUES (25, 5, 6, 2);
INSERT INTO `genealogy_dates_persons` (`id`, `genealogy_date_id`, `person_id`, `type_id`) VALUES (26, 5, 7, 1);
INSERT INTO `genealogy_dates_persons` (`id`, `genealogy_date_id`, `person_id`, `type_id`) VALUES (27, 5, 7, 2);
INSERT INTO `genealogy_dates_persons` (`id`, `genealogy_date_id`, `person_id`, `type_id`) VALUES (28, 5, 8, 1);
INSERT INTO `genealogy_dates_persons` (`id`, `genealogy_date_id`, `person_id`, `type_id`) VALUES (29, 5, 8, 2);
INSERT INTO `genealogy_dates_persons` (`id`, `genealogy_date_id`, `person_id`, `type_id`) VALUES (30, 5, 9, 1);
INSERT INTO `genealogy_dates_persons` (`id`, `genealogy_date_id`, `person_id`, `type_id`) VALUES (31, 5, 9, 2);
INSERT INTO `genealogy_dates_persons` (`id`, `genealogy_date_id`, `person_id`, `type_id`) VALUES (32, 5, 10, 1);
INSERT INTO `genealogy_dates_persons` (`id`, `genealogy_date_id`, `person_id`, `type_id`) VALUES (33, 5, 10, 2);
INSERT INTO `genealogy_dates_persons` (`id`, `genealogy_date_id`, `person_id`, `type_id`) VALUES (34, 5, 11, 1);
INSERT INTO `genealogy_dates_persons` (`id`, `genealogy_date_id`, `person_id`, `type_id`) VALUES (35, 5, 11, 2);
INSERT INTO `genealogy_dates_persons` (`id`, `genealogy_date_id`, `person_id`, `type_id`) VALUES (36, 5, 12, 1);
INSERT INTO `genealogy_dates_persons` (`id`, `genealogy_date_id`, `person_id`, `type_id`) VALUES (37, 5, 12, 2);
INSERT INTO `genealogy_dates_persons` (`id`, `genealogy_date_id`, `person_id`, `type_id`) VALUES (38, 5, 13, 1);
INSERT INTO `genealogy_dates_persons` (`id`, `genealogy_date_id`, `person_id`, `type_id`) VALUES (39, 5, 13, 2);
INSERT INTO `genealogy_dates_persons` (`id`, `genealogy_date_id`, `person_id`, `type_id`) VALUES (40, 5, 14, 1);
INSERT INTO `genealogy_dates_persons` (`id`, `genealogy_date_id`, `person_id`, `type_id`) VALUES (41, 5, 14, 2);
INSERT INTO `genealogy_dates_persons` (`id`, `genealogy_date_id`, `person_id`, `type_id`) VALUES (42, 5, 15, 1);
INSERT INTO `genealogy_dates_persons` (`id`, `genealogy_date_id`, `person_id`, `type_id`) VALUES (43, 5, 15, 2);
INSERT INTO `genealogy_dates_persons` (`id`, `genealogy_date_id`, `person_id`, `type_id`) VALUES (44, 5, 16, 1);
INSERT INTO `genealogy_dates_persons` (`id`, `genealogy_date_id`, `person_id`, `type_id`) VALUES (45, 5, 16, 2);
INSERT INTO `genealogy_dates_persons` (`id`, `genealogy_date_id`, `person_id`, `type_id`) VALUES (46, 5, 17, 1);
INSERT INTO `genealogy_dates_persons` (`id`, `genealogy_date_id`, `person_id`, `type_id`) VALUES (47, 5, 17, 2);
INSERT INTO `genealogy_dates_persons` (`id`, `genealogy_date_id`, `person_id`, `type_id`) VALUES (48, 5, 18, 1);
INSERT INTO `genealogy_dates_persons` (`id`, `genealogy_date_id`, `person_id`, `type_id`) VALUES (49, 5, 18, 2);


INSERT INTO `genealogy_dates_channels` (`id`, `genealogy_date_id`, `channel_id`)
    VALUES (1, 2, 1);
INSERT INTO `genealogy_dates_channels` (`id`, `genealogy_date_id`, `channel_id`)
    VALUES (2, 2, 2);
INSERT INTO `genealogy_dates_channels` (`id`, `genealogy_date_id`, `channel_id`)
    VALUES (3, 3, 3);
INSERT INTO `genealogy_dates_channels` (`id`, `genealogy_date_id`, `channel_id`)
    VALUES (4, 4, 5);
INSERT INTO `genealogy_dates_channels` (`id`, `genealogy_date_id`, `channel_id`)
    VALUES (5, 5, 6);
INSERT INTO `genealogy_dates_channels` (`id`, `genealogy_date_id`, `channel_id`)
    VALUES (6, 5, 7);