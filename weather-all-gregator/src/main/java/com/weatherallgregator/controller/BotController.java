package com.weatherallgregator.controller;

import com.weatherallgregator.dto.YandexForecast;
import com.weatherallgregator.jpa.entity.ForecastLocationEntity;
import com.weatherallgregator.jpa.entity.UserEntity;
import com.weatherallgregator.jpa.repo.UserRepo;
import com.weatherallgregator.mapper.UserMapper;
import com.weatherallgregator.mapper.YandexForecastMapper;
import com.weatherallgregator.model.FactYandexModel;
import com.weatherallgregator.model.YandexForecastModel;
import com.weatherallgregator.service.ForecastLocationService;
import com.weatherallgregator.service.YandexForecastService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.abilitybots.api.objects.MessageContext;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
@Slf4j
public class BotController {

    private final YandexForecastService yandexForecastService;
    private final ForecastLocationService forecastLocationService;
    private final UserRepo userRepo;

    @Transactional
    public void saveLocation(Update update) {

        Location location = update.getMessage().getLocation();
        ForecastLocationEntity locationEntity =
                forecastLocationService.saveLocation(location.getLatitude().floatValue(), location.getLongitude().floatValue());

        User userFrom = update.getMessage().getFrom();
        userRepo.findById(userFrom.getId())
                .or(createUser(userFrom))
                .ifPresent(user -> {
                    user.setForecastLocation(locationEntity);
                    userRepo.saveAndFlush(user);
                });
    }

    public String getFactForecast(MessageContext mc) {

        Optional<UserEntity> user = userRepo.findById(mc.user().getId());
        if (user.isEmpty()) return "Set the location first";

        return user.map(getForecastByUserSettings())
                .map(YandexForecastMapper::mapToFactYandexModel)
                .map(FactYandexModel::toRuStringResponse)
                .orElse("Error during retrieving forecast");
    }

    public String getForecast(MessageContext mc) {
        Optional<UserEntity> user = userRepo.findById(mc.user().getId());
        if (user.isEmpty()) return "Set the location first";

        return user.map(getForecastByUserSettings())
                .map(YandexForecastMapper::mapToYandexModel)
                .map(YandexForecastModel::toRuStringResponse)
                .orElse("Error during retrieving forecast");
    }

    private Function<UserEntity, YandexForecast> getForecastByUserSettings() {
        return userEntity -> {
            var lat = userEntity.getForecastLocation().getLat();
            var lon = userEntity.getForecastLocation().getLon();
            log.info("Returning forecast by params: lat = {}, lon = {}", lat, lon);
            return yandexForecastService.getForecast(UserMapper.mapToUser(userEntity));
        };
    }

    private Supplier<? extends Optional<? extends UserEntity>> createUser(User user) {
        return () -> Optional.of(new UserEntity(user.getId(), "GMT+3"));
    }
}
