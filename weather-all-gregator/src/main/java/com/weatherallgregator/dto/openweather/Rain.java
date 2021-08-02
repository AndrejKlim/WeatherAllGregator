package com.weatherallgregator.dto.openweather;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Rain {

    @JsonProperty("1h")
    int oneHour; // rain volume in 1 hour in mm
    @JsonProperty("3h")
    int threeHour;
}
