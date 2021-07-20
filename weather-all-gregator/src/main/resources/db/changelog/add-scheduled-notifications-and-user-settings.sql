create table forecast_location
(
    id  bigserial primary key,
    lat decimal not null,
    lon decimal not null
);
create table users
(
    id                   bigint primary key,
    time_zone            varchar(5)                               not null,
    forecast_location_id bigint references forecast_location (id) not null
);
create table scheduled_notification
(
    chat_id           varchar(10) primary key,
    notification_time varchar(5)                   not null,
    user_id           bigint references users (id) not null,
    forecast_type     varchar                      not null,
    sources           varchar                      not null
);

delete
from yandex_forecast;
alter table yandex_forecast
    add column forecast_location_id bigint references forecast_location (id) not null;

update api_call_counter
set api = 'YANDEX'
where api = 'yandex';