package com.weatherallgregator.jpa.entity;

import javax.persistence.*;

@Entity
@Table(name = "scheduled_notification")
public class ScheduledNotificationEntity {

    @Id
    @Column(name = "chat_id", length = 10)
    private String chatId;
    @Column(name = "notification_time", length = 5, nullable = false)
    private String notificationTime;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
    @Column(name = "forecast_type", nullable = false)
    private String forecastType;
    @Column(nullable = false)
    private String sources; // format - SOURCE,SOURCE,SOURCE...
    @Column(nullable = false)
    private Boolean executed;

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getNotificationTime() {
        return notificationTime;
    }

    public void setNotificationTime(String notificationTime) {
        this.notificationTime = notificationTime;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public String getForecastType() {
        return forecastType;
    }

    public void setForecastType(final String forecastType) {
        this.forecastType = forecastType;
    }

    public String getSources() {
        return sources;
    }

    public void setSources(final String sources) {
        this.sources = sources;
    }

    public Boolean getExecuted() {
        return executed;
    }

    public void setExecuted(final Boolean executed) {
        this.executed = executed;
    }
}
