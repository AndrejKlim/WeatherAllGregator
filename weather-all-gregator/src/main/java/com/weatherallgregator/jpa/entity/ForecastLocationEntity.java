package com.weatherallgregator.jpa.entity;

import javax.persistence.*;

@Entity
@Table(name = "forecast_location")
public class ForecastLocationEntity {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Float lat;
    @Column(nullable = false)
    private Float lon;

    public ForecastLocationEntity() {
    }

    public ForecastLocationEntity(Float lat, Float lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Float getLat() {
        return lat;
    }

    public void setLat(Float lat) {
        this.lat = lat;
    }

    public Float getLon() {
        return lon;
    }

    public void setLon(Float lon) {
        this.lon = lon;
    }

    @Override
    public String toString() {
        return "ForecastLocationEntity{" +
                "lat=" + lat +
                ", lon=" + lon +
                '}';
    }
}
