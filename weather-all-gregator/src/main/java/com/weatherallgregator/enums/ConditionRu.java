package com.weatherallgregator.enums;

public enum ConditionRu {
    CLEAR("ясно"),
    PARTLY_CLOUDY("малооблачно"),
    CLOUDY("облачно с прояснениями"),
    OVERCAST("пасмурно"),
    DRIZZLE("морось"),
    LIGHT_RAIN("небольшой дождь"),
    RAIN("дождь"),
    MODERATE_RAIN("умеренно сильный дождь"),
    HEAVY_RAIN("сильный дождь"),
    CONTINUOUS_HEAVY_RAIN("длительный сильный дождь"),
    SHOWERS("линь"),
    WET_SNOW("дождь со снегом"),
    LIGHT_SNOW("небольшой снег"),
    SNOW("снег"),
    SNOW_SHOWERS("снегопад"),
    HAIL("град"),
    THUNDERSTORM("гроза"),
    THUNDERSTORM_WITH_RAIN("дождь с грозой"),
    THUNDERSTORM_WITH_HAIL("гроза с градом");

    public final String value;

    public static ConditionRu valueOfCondition(String condition){
        for (ConditionRu c : values()){
            if (c.value.equals(condition)){
                return c;
            }
        }
        return null;
    }

    ConditionRu(String value) {
        this.value = value;
    }
}
