package com.weatherallgregator.dto.weatherbit;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class WeatherData {

    float lat;
    float lon;
    String sunrise;
    String sunset;
    String timezone;
    String station;
    @JsonProperty("ob_time")
    String observationTime;
    String datetime;
    @JsonProperty("ts")
    Long lastObservationTime;
    String cityName;
    String countryCode;
    String stateCode;
    @JsonProperty("pres")
    float pressure;
    @JsonProperty("slp")
    float seaLevelPressure;
    @JsonProperty("wind_spd")
    float windSpeed;
    @JsonProperty("wind_dir")
    String windDirectionDegrees;
    @JsonProperty("wind_cdir")
    String windDirection;
    @JsonProperty("wind_cdir_full")
    String windDirectionFull;
    float temp;
    @JsonProperty("app_temp")
    float feelsLike;
    @JsonProperty("rh")
    int relativeHumidity;
    @JsonProperty("dewpt")
    float dewPoint;
    int clouds;
    @JsonProperty("pod")
    String partOfTheDay;
    Weather weather;
    @JsonProperty("vis")
    int visibility;
    @JsonProperty("precip")
    float precipitation;
    float snow;
    int uv;
    @JsonProperty("aqi")
    int airQualityIndex;
    @JsonProperty("dhi")
    int diffuseHorisontalSolarIrradiance;
    @JsonProperty("dni")
    int directNormalSolarIrradiance;
    @JsonProperty("ghi")
    float globalHorizontalSolarIrradiance;
    @JsonProperty("solar_rad")
    int estimatedSolarRadiation;
    @JsonProperty("elev_angle")
    int solarElevationAngle;
    @JsonProperty("h_angle")
    float solarHourAngle;
}
