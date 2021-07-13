package com.weatherallgregator.model;

import com.weatherallgregator.enums.ConditionRu;
import com.weatherallgregator.enums.DayTimeRu;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.stream.Collectors;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class YandexForecastModel {
    List<YandexPartModel> parts;

    public String toRuStringResponse() {
        if (parts.size() != 2) return null;

        return parts.stream()
                .map(p -> String.format("Погода на %s\n" +
                                "Средняя температура - %d ℃\n" +
                                "Облачность или осадки - %s\n" +
                                "Скорость ветра - %.1f м/с\n" +
                                "Давление - %d мм рт. ст.\n" +
                                "Влажность - %d %%\n" +
                                "Вероятность осадков - %d %%\n" +
                                "Объем вероятных осадков - %d мм",
                        DayTimeRu.valueOf(p.getDayTime().name()).value,
                        p.getAverageTemp(),
                        ConditionRu.valueOf(p.getCondition().name()).value,
                        p.getWindSpeed(),
                        p.getPressureMm(),
                        p.getHumidity(),
                        p.getPrecipitationProbability(),
                        p.getPrecipitationMm()))
                .collect(Collectors.joining("\n==============\n"));
    }
}
