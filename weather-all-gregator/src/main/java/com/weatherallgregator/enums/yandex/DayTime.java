package com.weatherallgregator.enums.yandex;

public enum DayTime {
    NIGHT("night"),
    MORNING("morning"),
    DAY("day"),
    EVENING("evening");

    public final String value;

    public static DayTime valueOfDayTime(String dayTime){
        for (DayTime d : values()){
            if (d.value.equals(dayTime)){
                return d;
            }
        }
        return null;
    }

    DayTime(String value) {
        this.value = value;
    }
}
