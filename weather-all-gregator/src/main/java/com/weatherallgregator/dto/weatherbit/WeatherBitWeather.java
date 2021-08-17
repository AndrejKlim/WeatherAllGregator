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

        WeatherData data = this.data.get(0);
        return String.format("*Weatherbit*\n" +
                        "Погода на данный момент\n" +
                        "Температура - %.0f ℃\n" +
                        "Облачность или осадки - %s\n" +
                        "Скорость ветра - %.1f м/с\n" +
                        "Давление - %d мм рт. ст.\n" +
                        "Влажность - %d %%",
                data.getTemp(),
                data.getWeather().getDescription(),
                data.getWindSpeed(),
                ConvertUtils.hPaToMm((int) data.getPressure()),
                data.getRelativeHumidity());
    }
}
