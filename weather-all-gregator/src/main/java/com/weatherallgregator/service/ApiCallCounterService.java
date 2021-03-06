package com.weatherallgregator.service;

import com.weatherallgregator.enums.ApiCallLimit;
import com.weatherallgregator.enums.ForecastSource;
import com.weatherallgregator.jpa.entity.ApiCallCounter;
import com.weatherallgregator.jpa.repo.ApiCallCounterRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class ApiCallCounterService {

    private final ApiCallCounterRepo repo;

    public boolean canApiCallBePerformed(ForecastSource apiName){
        Optional<ApiCallCounter> apiCallCounter = repo.findByApi(apiName.name());
        if (needsReset(apiCallCounter)){
            resetCounter(apiCallCounter); // mutates apiCallCounter here by setting 0 to counter and changing reset time
        }
        return apiCallCounter.isPresent() && apiCallCounter.get().getCounter() < ApiCallLimit.valueOf(apiName.name()).value;
    }

    public void incrementApiCallCounter(ForecastSource apiName){
        Optional<ApiCallCounter> apiCallCounter = repo.findByApi(apiName.name());
        apiCallCounter.ifPresent(counter -> {
            counter.setCounter(counter.getCounter() + 1);
            repo.save(counter);
        });
    }

    private boolean needsReset(Optional<ApiCallCounter> apiCallCounter) {
        return apiCallCounter.isPresent()
               && apiCallCounter.get().getCounterResetAt() < LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
    }

    private void resetCounter(Optional<ApiCallCounter> apiCallCounter) {
        var newResetTime =
                LocalDateTime.now().plusDays(1).withHour(7).withMinute(0).withSecond(0).truncatedTo(ChronoUnit.SECONDS);

        apiCallCounter.ifPresent(counter -> {
            counter.setCounterResetAt(newResetTime.toEpochSecond(ZoneOffset.UTC));
            counter.setCounter(0);
            repo.save(counter);

            log.info("{} Counter reset", counter.getApi());
        });
    }

}
