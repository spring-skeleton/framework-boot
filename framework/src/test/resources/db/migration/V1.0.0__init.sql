CREATE TABLE `role` (
    `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(32) NOT NULL COMMENT '',
    `code` VARCHAR(32) NOT NULL COMMENT 'code',
    `created_time` timestamp NOT NULL default current_timestamp,
    `updated_time` timestamp NOT NULL default current_timestamp,
    PRIMARY KEY(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ;

CREATE TABLE `org` (
    `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(32) NOT NULL COMMENT '',
    `code` VARCHAR(32) NOT NULL COMMENT 'code',
    `created_time` timestamp NOT NULL default current_timestamp,
    `updated_time` timestamp NOT NULL default current_timestamp,
    PRIMARY KEY(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;