DROP TABLE IF EXISTS `genealogy_dates_channels`;
DROP TABLE IF EXISTS `genealogy_dates_persons`;
DROP TABLE IF EXISTS `genealogy_dates_types`;
DROP TABLE IF EXISTS `genealogy_dates`;
DROP TABLE IF EXISTS `verification_email_tokens`;
DROP TABLE IF EXISTS `channels_telegram_details`;
DROP TABLE IF EXISTS `channels_email_details`;
DROP TABLE IF EXISTS `channels`;
DROP TABLE IF EXISTS `channel_types`;
DROP TABLE IF EXISTS `recipients`;

CREATE TABLE recipients (
    `id` int not null primary key AUTO_INCREMENT,
    `uuid` varchar(255) not null unique
);

CREATE TABLE channel_types (
    `id` int not null primary key AUTO_INCREMENT,
    `code` varchar(50) not null unique
);

CREATE TABLE channels (
    `id` int not null primary key AUTO_INCREMENT,
    `type_id` int not null,
    `recipient_id` int not null,
    constraint `fk_channels_type_id` foreign key(`type_id`) references `channel_types`(`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    constraint `fk_channels_recipient_id` foreign key(`recipient_id`) references `recipients`(`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    UNIQUE INDEX `unique_channels_type_id_recipient_id` (`type_id`, `recipient_id`)
);

CREATE TABLE channels_email_details (
    `channel_id` int not null unique,
	`email` varchar(255) not null,
	constraint `fk_channels_email_channel_id` foreign key(`channel_id`) references `channels`(`id`) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE channels_telegram_details (
    `channel_id` int not null unique,
	`chat_id` varchar(255) not null unique,
	`username` varchar(255) not null,
	constraint `fk_channels_telegram_channel_id` foreign key(`channel_id`) references `channels`(`id`) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE verification_email_tokens (
    `id` int not null primary key AUTO_INCREMENT,
    `recipient_id` int not null,
    `email` varchar(255) not null,
    `token` varchar(255) not null unique,
    `expires_at` datetime not null,
    `created_at` datetime default CURRENT_TIMESTAMP,
    constraint `fk_verification_tokens_recipient_id` foreign key(`recipient_id`) references `recipients`(`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    INDEX `idx_verification_tokens_token` (`token`)
);

/* uuid - из БД danshin_id */
CREATE TABLE genealogy_dates (
    `id` int not null primary key AUTO_INCREMENT,
    `recipient_id` int not null unique,
    `send_time` time not null,
    `timezone` varchar(50),
    `days_before` int not null,
    `days_after` int not null,
    constraint `fk_genealogy_dates_recipient_id` foreign key(`recipient_id`) references `recipients`(`id`) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE genealogy_dates_types (
    `id` int not null primary key AUTO_INCREMENT,
	`name` varchar(255) not null
);

/* person_id из БД danshin_genealogy */
CREATE TABLE genealogy_dates_persons (
    `id` int not null primary key AUTO_INCREMENT,
	`genealogy_date_id` int not null,
	`person_id` int not null,
	`type_id` int not null,
	constraint `fk_genealogy_dates_persons_genealogy_date_id` foreign key(`genealogy_date_id`) references `genealogy_dates`(`id`) ON DELETE CASCADE ON UPDATE CASCADE,
	constraint `fk_genealogy_dates_persons_type_id` foreign key(`type_id`) references `genealogy_dates_types`(`id`) ON DELETE CASCADE ON UPDATE CASCADE,
	UNIQUE INDEX `unique_genealogy_persons_genealogy_date_id_person_id_type_id` (`genealogy_date_id`, `person_id`, `type_id`),
	INDEX `idx_genealogy_dates_persons_person_id` (`person_id`)
);

CREATE TABLE genealogy_dates_channels (
    `id` int not null primary key AUTO_INCREMENT,
    `genealogy_date_id` int not null,
    `channel_id` int not null,
    constraint `fk_genealogy_dates_channels_genealogy_date_id` foreign key(`genealogy_date_id`) references `genealogy_dates`(`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    constraint `fk_genealogy_dates_channels_channel_id` foreign key(`channel_id`) references `channels`(`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    UNIQUE INDEX `unique_genealogy_dates_channels_genealogy_date_id_channel_id` (`genealogy_date_id`, `channel_id`)
);
