insert into api_call_counter(api, counter, counter_reset_at)
values ('OPEN_WEATHER', 0, EXTRACT(epoch from now()));

delete from yandex_forecast;
alter table yandex_forecast rename to forecast;
alter table forecast add column source varchar not null;