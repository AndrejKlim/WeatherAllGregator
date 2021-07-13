package com.weatherallgregator.enums;

public enum DayTimeRu {
    NIGHT("ночь"),
    MORNING("утро"),
    DAY("день"),
    EVENING("вечер");

    public final String value;

    public static DayTimeRu valueOfDayTime(String dayTime){
        for (DayTimeRu d : values()){
            if (d.value.equals(dayTime)){
                return d;
            }
        }
        return null;
    }

    DayTimeRu(String value) {
        this.value = value;
    }
}
