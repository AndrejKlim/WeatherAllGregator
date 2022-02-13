package com.weatherallgregator.dto.weatherbit;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.weatherallgregator.dto.ForecastInfo;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherBitDailyForecast implements ForecastInfo {

    List<WeatherBitDailyData> data;

    @Override
    public List<String> toRuForecastResponse() {
        final var dataCut = new ArrayList<WeatherBitDailyData>();
        for (int i = 0; i < 3; i++) {
            dataCut.add(data.get(i));
        }
        final String forecastTemplate = """
                                *Weatherbit*
                                Погода на %s
                                Температура: %.1f ℃
                                Максимальная температура: %.1f ℃
                                Минимальная Температура: %.1f ℃
                                Облачность или осадки - %s
                                Скорость ветра - %.1f м/с
                                Давление - %d мм рт. ст.
                                Влажность - %d %%""";

        return dataCut.stream()
                .map(d -> String.format(forecastTemplate,
                        LocalDateTime.ofInstant(Instant.ofEpochSecond(d.getTs()), ZoneId.of("GMT+3"))
                                .format(TimeDayDateFormat.HOUR_DAY),
                        d.getTemp(),
                        d.getHighTemp(),
                        d.getLowTemp(),
                        d.getWeather().getDescription(),
                        d.getWindSpeed(),
                        ConvertUtils.hPaToMm(d.getPres()),
                        d.getRh()))
                .toList();
    }

}
