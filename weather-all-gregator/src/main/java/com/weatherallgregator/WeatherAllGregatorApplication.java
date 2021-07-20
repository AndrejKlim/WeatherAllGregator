package com.weatherallgregator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WeatherAllGregatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(WeatherAllGregatorApplication.class, args);
    }

}
