package com.weatherallgregator.jpa.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "api_call_counter")
public class ApiCallCounter {

    @Id
    private Long id;
    private String api;
    private Integer counter;
    private Long counterResetAt;

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
