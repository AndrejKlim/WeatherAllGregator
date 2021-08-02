package com.weatherallgregator.dto.yandex;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.weatherallgregator.dto.ForecastInfo;
import com.weatherallgregator.dto.WeatherInfo;
import com.weatherallgregator.enums.yandex.Condition;
import com.weatherallgregator.enums.yandex.ConditionRu;
import com.weatherallgregator.enums.yandex.DayTime;
import com.weatherallgregator.enums.yandex.DayTimeRu;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.stream.Collectors;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class YandexForecast implements WeatherInfo, ForecastInfo {

    Long now;
    String nowDt;
    Info info;
    Fact fact;
    Forecast forecast;

    @Override
    public String toRuWeatherResponse() {
        return String.format("*Яндекс погода*\n" +
                        "Погода на данный момент\n" +
                        "Температура - %d ℃\n" +
                        "Облачность или осадки - %s\n" +
                        "Скорость ветра - %d м/с\n" +
                        "Давление - %d мм рт. ст.\n" +
                        "Влажность - %d %%",
                fact.getTemp(),
                ConditionRu.valueOf(Condition.valueOfCondition(fact.getCondition()).name()).value,
                fact.getWindSpeed(),
                fact.getPressureMm(),
                fact.getHumidity());
    }

    @Override
    public String toRuForecastResponse() {
        return forecast.getParts().stream()
                .map(p -> String.format("Яндекс погода\n" +
                                "Погода на %s\n" +
                                "Средняя температура - %d ℃\n" +
                                "Облачность или осадки - %s\n" +
                                "Скорость ветра - %.1f м/с\n" +
                                "Давление - %d мм рт. ст.\n" +
                                "Влажность - %d %%\n" +
                                "Вероятность осадков - %d %%\n" +
                                "Объем вероятных осадков - %d мм",
                        DayTimeRu.valueOf(DayTime.valueOfDayTime(p.getPartName()).name()).value,
                        p.getTempAvg(),
                        ConditionRu.valueOf(Condition.valueOfCondition(p.getCondition()).name()).value,
                        p.getWindSpeed(),
                        p.getPressureMm(),
                        p.getHumidity(),
                        p.getPrecProb(),
                        p.getPrecMm()))
                .collect(Collectors.joining("\n==============\n"));
    }
}
