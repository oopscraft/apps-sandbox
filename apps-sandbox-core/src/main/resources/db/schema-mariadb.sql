-- apps_sandbox_sample
create table `apps_sandbox_sample` (
    `id` varchar(64) not null,
    `modify_date_time` timestamp,
    `modify_user_id` varchar(32),
    `system_data_yn` varchar(1),
    `big_decimal` decimal(19,2),
    `crypto_text` mediumtext,
    `double_number` double,
    `lob_text` mediumtext,
    `local_date` date,
    `local_date_time` timestamp,
    `long_number` bigint,
    `name` varchar(255) not null,
    `number` integer,
    `sql_date` date,
    `timestamp` timestamp,
    `util_date` timestamp,
    primary key (`id`)
);

-- apps_sandbox_sample_item
create table `apps_sandbox_sample_item` (
    `id` varchar(64) not null,
    `sample_id` varchar(64) not null,
    `modify_date_time` timestamp,
    `modify_user_id` varchar(32),
    `system_data_yn` varchar(1),
    `local_date` date,
    `local_date_time` timestamp,
    `name` varchar(255) not null,
    `number` integer,
    primary key (`id`, `sample_id`)
);

-- apps_sandbox_sample_backup
create table `apps_sandbox_sample_backup` (
    `id` varchar(64) not null,
    `modify_date_time` timestamp,
    `modify_user_id` varchar(32),
    `system_data_yn` varchar(1),
    `big_decimal` decimal(19,2),
    `crypto_text` mediumtext,
    `double_number` double,
    `lob_text` mediumtext,
    `local_date` date,
    `local_date_time` timestamp,
    `long_number` bigint,
    `name` varchar(255) not null,
    `number` integer,
    `sql_date` date,
    `timestamp` timestamp,
    `util_date` timestamp,
    primary key (`id`)
);

-- apps_sandbox_sample_item_backup
create table `apps_sandbox_sample_item_backup` (
    `id` varchar(64) not null,
    `sample_id` varchar(64) not null,
    `modify_date_time` timestamp,
    `modify_user_id` varchar(32),
    `system_data_yn` varchar(1),
    `local_date` date,
    `local_date_time` timestamp,
    `name` varchar(255) not null,
    `number` integer,
    primary key (`id`, `sample_id`)
);

-- apps_sandbox_sample_error
create table `apps_sandbox_sample_error` (
    `id` varchar(64) not null,
    `modify_date_time` timestamp,
    `modify_user_id` varchar(32),
    `system_data_yn` varchar(1),
    `big_decimal` decimal(19,2),
    `crypto_text` mediumtext,
    `double_number` double,
    `lob_text` mediumtext,
    `local_date` date,
    `local_date_time` timestamp,
    `long_number` bigint,
    `name` varchar(255) not null,
    `number` integer,
    `sql_date` date,
    `timestamp` timestamp,
    `util_date` timestamp,
    primary key (`id`)
);


