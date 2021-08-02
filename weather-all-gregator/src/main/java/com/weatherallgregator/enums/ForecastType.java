package com.weatherallgregator.enums;

public enum ForecastType {

    WEATHER("Погода на настоящий момент"),
    FORECAST("Ближайший прогноз");

    public final String value;

    public static ForecastType valueOfType(final String value){
        for (ForecastType d : values()){
            if (d.value.equals(value)){
                return d;
            }
        }
        return WEATHER;
    }

    ForecastType(String value) {
        this.value = value;
    }
}
