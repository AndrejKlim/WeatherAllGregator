package com.weatherallgregator.service;

import com.weatherallgregator.jpa.entity.ApiCallCounter;
import com.weatherallgregator.jpa.repo.ApiCallCounterRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ApiCallCounterService {

    private static final int YANDEX_API_CALL_DAY_LIMIT = 50;

    private final ApiCallCounterRepo repo;

    public boolean canApiCallBePerformed(String apiName){
        Optional<ApiCallCounter> apiCallCounter = repo.findByApi(apiName);
        if (needsReset(apiCallCounter)){
            resetCounter(apiCallCounter); // mutates apiCallCounter here by setting 0 to counter and changing reset time
        }
        return apiCallCounter.isPresent() && apiCallCounter.get().getCounter() < YANDEX_API_CALL_DAY_LIMIT; // TODO add choosing limit by api type
    }

    public void incrementApiCallCounter(String apiName){
        Optional<ApiCallCounter> apiCallCounter = repo.findByApi(apiName);
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

        apiCallCounter.get().setCounterResetAt(newResetTime.toEpochSecond(ZoneOffset.UTC));
        apiCallCounter.get().setCounter(0);

        repo.save(apiCallCounter.get());
    }

}
