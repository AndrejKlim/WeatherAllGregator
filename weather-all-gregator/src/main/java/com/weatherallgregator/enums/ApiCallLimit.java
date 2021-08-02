package com.weatherallgregator.enums;

public enum ApiCallLimit {

    YANDEX(50),
    OPEN_WEATHER(86400);

    public final int value;

    ApiCallLimit(int value) {
        this.value = value;
    }
}
