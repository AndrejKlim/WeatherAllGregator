package com.weatherallgregator.jpa.entity;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    private Long id;
    @Column(name = "time_zone", length = 5, nullable = false)
    private String timeZone;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "forecast_location_id")
    private ForecastLocationEntity forecastLocation;
    @OneToMany(mappedBy = "user")
    private Set<ScheduledNotificationEntity> notifications;

    public UserEntity() {
    }

    public UserEntity(Long id, String timeZone) {
        this.id = id;
        this.timeZone = timeZone;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ForecastLocationEntity getForecastLocation() {
        return forecastLocation;
    }

    public void setForecastLocation(ForecastLocationEntity forecastLocation) {
        this.forecastLocation = forecastLocation;
    }

    public Set<ScheduledNotificationEntity> getNotifications() {
        return notifications;
    }

    public void setNotifications(Set<ScheduledNotificationEntity> notifications) {
        this.notifications = notifications;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }
}
