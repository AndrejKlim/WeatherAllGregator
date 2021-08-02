package com.weatherallgregator.jpa.entity;

import javax.persistence.*;

@Entity
@Table(name = "forecast")
public class ForecastEntity {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(name = "created_at", nullable = false)
    private Long createdAt;
    private String forecast; // in json
    @Column(nullable = false)
    private String source;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "forecast_location_id")
    private ForecastLocationEntity forecastLocation;

    public ForecastEntity() {
    }

    public ForecastEntity(Long createdAt, String forecast, String source, ForecastLocationEntity forecastLocation) {
        this.createdAt = createdAt;
        this.forecast = forecast;
        this.source = source;
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

    public ForecastLocationEntity getForecastLocation() {
        return forecastLocation;
    }

    public void setForecastLocation(ForecastLocationEntity forecastLocation) {
        this.forecastLocation = forecastLocation;
    }

    public String getSource() {
        return source;
    }

    public void setSource(final String source) {
        this.source = source;
    }

    @Override
    public String toString() {
        return "ForecastEntity{" +
                "createdAt=" + createdAt +
                ", source='" + source + '\'' +
                '}';
    }
}
