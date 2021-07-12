package com.weatherallgregator.jpa.entity;

import javax.persistence.*;

@Entity
@Table(name = "api_call_counter")
public class ApiCallCounter {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    private String api;
    private Integer counter;
    @Column(name = "counter_reset_at")
    private Long counterResetAt;

    public ApiCallCounter() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public Integer getCounter() {
        return counter;
    }

    public void setCounter(Integer counter) {
        this.counter = counter;
    }

    public Long getCounterResetAt() {
        return counterResetAt;
    }

    public void setCounterResetAt(Long counterResetAt) {
        this.counterResetAt = counterResetAt;
    }
}
