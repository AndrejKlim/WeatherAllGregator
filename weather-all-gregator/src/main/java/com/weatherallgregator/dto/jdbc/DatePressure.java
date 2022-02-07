package com.weatherallgregator.dto.jdbc;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class DatePressure {
    long createdAt;
    long timestamp;
    int pressure;
}
