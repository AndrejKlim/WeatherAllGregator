package com.weatherallgregator.jpa.entity;

import javax.persistence.*;

@Entity
@Table(name = "yandex_forecast")
public class YandexForecastEntity {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(name = "created_at")
    private Long createdAt;
    private String forecast; // in json
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "forecast_location_id")
    private ForecastLocationEntity forecastLocation;

    public YandexForecastEntity() {
    }

    public YandexForecastEntity(Long createdAt, String forecast, ForecastLocationEntity forecastLocation) {
        this.createdAt = createdAt;
        this.forecast = forecast;
        this.forecastLocation = forecastLocation;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public String getForecast() {
        return forecast;
    }

    public void setForecast(String forecast) {
        this.forecast = forecast;
    }

    @Override
    public String toString() {
        return "YandexForecastEntity{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", forecast='" + forecast + '\'' +
                '}';
    }

    public ForecastLocationEntity getForecastLocation() {
        return forecastLocation;
    }

    public void setForecastLocation(ForecastLocationEntity forecastLocation) {
        this.forecastLocation = forecastLocation;
    }
}
