package com.weatherallgregator.dto.openweather;

import com.weatherallgregator.dto.WeatherInfo;
import com.weatherallgregator.util.ConvertUtils;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OpenWeatherForecast implements WeatherInfo {

    Coordinates coord;
    List<Weather> weather;
    String base;
    Main main;
    int visibility;
    Wind wind;
    Rain rain;
    Snow snow;
    Clouds clouds;
    long dt;
    Sys sys;
    int timezone;
    int id;
    String name;
    int cod;

    @Override
    public String toRuWeatherResponse() {
        return String.format("*Open weather*\n" +
                        "Погода на данный момент\n" +
                        "Температура - %.0f ℃\n" +
                        "Облачность или осадки - %s\n" +
                        "Скорость ветра - %d м/с\n" +
                        "Давление - %d мм рт. ст.\n" +
                        "Влажность - %d %%",
                main.getTemp(),
                weather.get(0).getDescription(),
                wind.getSpeed(),
                ConvertUtils.hPaToMm(main.getPressure()),
                main.getHumidity());
    }
}
