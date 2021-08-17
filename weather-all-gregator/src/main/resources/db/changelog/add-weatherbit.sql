insert into api_call_counter(api, counter, counter_reset_at)
values ('WEATHERBIT', 0, EXTRACT(epoch from now()));
