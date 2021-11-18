package com.weatherallgregator.dto.openweather;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.weatherallgregator.dto.ForecastInfo;
import com.weatherallgregator.dto.WeatherInfo;
import com.weatherallgregator.util.ConvertUtils;
import com.weatherallgregator.util.TimeDayDateFormat;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OpenWeatherForecast implements WeatherInfo, ForecastInfo {

    float lat;
    float lon;
    String timezone;
    @JsonProperty("timezone_offset")
    int timezoneOffset;
    Current current;
    List<Minutely> minutely;
    List<Hourly> hourly;
    List<Daily> daily;
    List<Alert> alerts;

    @Override
    public String toRuWeatherResponse() {
        return String.format("""
                        *Open weather*
                        Погода на данный момент
                        Температура: %s%.0f ℃
                        Облачность или осадки - %s
                        Скорость ветра - %d м/с
                        Давление - %d мм рт. ст.
                        Влажность - %d %%""",
                current.getTemp() > 0 ? "+" : "-",
                current.getTemp(),
                current.getWeather().get(0).getDescription(),
                current.getWindSpeed(),
                ConvertUtils.hPaToMm(current.getPressure()),
                current.getHumidity());
    }

    @Override
    public List<String> toRuForecastResponse() {
        final List<Hourly> hourlyCut = new ArrayList<>();
        for (int i = 0; i < hourly.size(); i += 6 ) {
            hourlyCut.add(hourly.get(i));
        }
        return hourlyCut.stream()
                .map(hour -> String.format("""
                                *Open weather*
                                Погода на %s
                                Температура - %.0f ℃
                                Облачность или осадки - %s
                                Скорость ветра - %d м/с
                                Давление - %d мм рт. ст.
                                Влажность - %d %%""",
                        LocalDateTime.ofInstant(Instant.ofEpochSecond(hour.getDt()), ZoneId.of("GMT+3"))
                                .format(TimeDayDateFormat.HOUR_DAY),
                        hour.getTemp(),
                        hour.getWeather().get(0).getDescription(),
                        hour.getWindSpeed(),
                        ConvertUtils.hPaToMm(hour.getPressure()),
                        hour.getHumidity()))
                .toList();
    }
}
