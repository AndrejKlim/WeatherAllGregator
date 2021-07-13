package com.weatherallgregator.enums;

public enum Condition {
    CLEAR("clear"),
    PARTLY_CLOUDY("partly-cloudy"),
    CLOUDY("cloudy"),
    OVERCAST("overcast"),
    DRIZZLE("drizzle"),
    LIGHT_RAIN("light-rain"),
    RAIN("rain"),
    MODERATE_RAIN("moderate-rain"),
    HEAVY_RAIN("heavy-rain"),
    CONTINUOUS_HEAVY_RAIN("continuous-heavy-rain"),
    SHOWERS("showers"),
    WET_SNOW("wet-snow"),
    LIGHT_SNOW("light-snow"),
    SNOW("snow"),
    SNOW_SHOWERS("snow-showers"),
    HAIL("hail"),
    THUNDERSTORM("thunderstorm"),
    THUNDERSTORM_WITH_RAIN("thunderstorm-with-rain"),
    THUNDERSTORM_WITH_HAIL("thunderstorm-with-hail");

    public final String value;

    public static Condition valueOfCondition(String condition){
        for (Condition c : values()){
            if (c.value.equals(condition)){
                return c;
            }
        }
        return null;
    }

    Condition(String value) {
        this.value = value;
    }
}
