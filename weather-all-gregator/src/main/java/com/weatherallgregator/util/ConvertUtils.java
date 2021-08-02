package com.weatherallgregator.util;

public class ConvertUtils {

    private ConvertUtils(){

    }

    public static int hPaToMm(final int pressureHPa){
        return (int) (pressureHPa * 0.75006);
    }
}
