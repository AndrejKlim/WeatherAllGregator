package com.weatherallgregator.dto.weatherbit;

import com.weatherallgregator.dto.WeatherInfo;
import com.weatherallgregator.util.ConvertUtils;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;


@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WeatherBitWeather implements WeatherInfo {

    int count;
    List<WeatherData> data;

    @Override
    public String toRuWeatherResponse() {
        if (data.size() != 1){
            return "Something wrong with WeatherBit weather response";
        }

        WeatherData weatherData = this.data.get(0);
        return String.format("""
                        *Weatherbit*
                        Погода на данный момент
                        Температура: %.0f ℃
                        Облачность или осадки - %s
                        Скорость ветра - %.1f м/с
                        Давление - %d мм рт. ст.
                        Влажность - %d %%""",
                weatherData.getTemp(),
                weatherData.getWeather().getDescription(),
                weatherData.getWindSpeed(),
                ConvertUtils.hPaToMm((int) weatherData.getPressure()),
                weatherData.getRelativeHumidity());
    }
}
