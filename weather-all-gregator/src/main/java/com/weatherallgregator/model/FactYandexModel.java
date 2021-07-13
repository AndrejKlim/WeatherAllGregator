package com.weatherallgregator.model;

import com.weatherallgregator.enums.Condition;
import com.weatherallgregator.enums.ConditionRu;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FactYandexModel {

    int temp;
    Condition condition;
    float windSpeed;
    int pressureMm;
    int humidity;

    public String toRuStringResponse(){
        return String.format("Погода на данный момент\n" +
                "Температура - %d ℃\n" +
                "Облачность или осадки - %s\n" +
                "Скорость ветра - %.1f м/с\n" +
                "Давление - %d мм рт. ст.\n" +
                "Влажность - %d %%", temp, ConditionRu.valueOf(condition.name()).value, windSpeed, pressureMm, humidity);
    }
}
