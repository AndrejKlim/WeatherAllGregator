create table api_call_counter
(
    id               bigserial primary key,
    api              text   not null,
    counter          int    not null,
    counter_reset_at bigint not null
);
create table yandex_forecast
(
    id         bigserial primary key,
    created_at bigint not null,
    forecast   text
);

insert into api_call_counter(api, counter, counter_reset_at)
values ('yandex', 0, EXTRACT(epoch from now()))