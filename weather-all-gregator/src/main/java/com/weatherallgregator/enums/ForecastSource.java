package com.weatherallgregator.enums;

public enum ForecastSource {

    ALL("Из всех источников"),
    YANDEX("Яндекс погода");

    public final String value;

    public static ForecastSource valueOfSource(final String value){
        for (ForecastSource d : values()){
            if (d.value.equals(value)){
                return d;
            }
        }
        return null;
    }

    ForecastSource(String value) {
        this.value = value;
    }
}
