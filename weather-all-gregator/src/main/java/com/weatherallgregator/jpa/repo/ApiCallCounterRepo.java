package com.weatherallgregator.jpa.repo;

import com.weatherallgregator.jpa.entity.ApiCallCounter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApiCallCounterRepo extends JpaRepository<ApiCallCounter, Long> {

    Optional<ApiCallCounter> findByApi(String api);
}
