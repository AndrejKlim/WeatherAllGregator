package com.weatherallgregator.controller;

import com.weatherallgregator.dto.ScheduledNotification;
import com.weatherallgregator.dto.yandex.YandexForecast;
import com.weatherallgregator.enums.ForecastSource;
import com.weatherallgregator.enums.ForecastType;
import com.weatherallgregator.enums.ScheduledNotificationCreatingPipeline;
import com.weatherallgregator.jpa.entity.ForecastLocationEntity;
import com.weatherallgregator.jpa.entity.ScheduledNotificationEntity;
import com.weatherallgregator.jpa.entity.UserEntity;
import com.weatherallgregator.jpa.repo.UserRepo;
import com.weatherallgregator.mapper.UserMapper;
import com.weatherallgregator.mapper.YandexForecastMapper;
import com.weatherallgregator.model.FactYandexModel;
import com.weatherallgregator.model.YandexForecastModel;
import com.weatherallgregator.service.ForecastLocationService;
import com.weatherallgregator.service.ScheduledNotificationService;
import com.weatherallgregator.service.YandexForecastService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import static com.weatherallgregator.enums.ScheduledNotificationCreatingPipeline.*;
import static com.weatherallgregator.mapper.ScheduledNotificationMapper.mapToScheduledNotificationEntity;

@Component
@RequiredArgsConstructor
@Slf4j
public class BotController {

    public static final String NEW_USER_RESPONSE = "Вы первый раз используете систему." +
            " Отправьте свою геолокацию для получения прогноза.";
    private final YandexForecastService yandexForecastService;
    private final ForecastLocationService forecastLocationService;
    private final ScheduledNotificationService scheduledNotificationService;
    private final UserRepo userRepo;
    private ScheduledNotification scheduledNotification;

    @Transactional
    public void saveLocation(final Update update) {

        Location location = update.getMessage().getLocation();
        var lat = BigDecimal.valueOf(location.getLatitude()).setScale(2, RoundingMode.HALF_UP).floatValue();
        var lon = BigDecimal.valueOf(location.getLongitude()).setScale(2, RoundingMode.HALF_UP).floatValue();

        ForecastLocationEntity locationEntity =
                forecastLocationService.saveLocation(lat, lon);

        User userFrom = update.getMessage().getFrom();
        userRepo.findById(userFrom.getId())
                .or(createUser(userFrom))
                .ifPresent(user -> {
                    user.setForecastLocation(locationEntity);
                    userRepo.saveAndFlush(user);
                });
    }

    public String getFactForecast(final Update update) {

        Optional<UserEntity> user = userRepo.findById(update.getCallbackQuery().getFrom().getId());
        if (user.isEmpty()) return NEW_USER_RESPONSE;

        return user.map(getForecastByUserSettings())
                .map(YandexForecastMapper::mapToFactYandexModel)
                .map(FactYandexModel::toRuStringResponse)
                .orElse("Error during retrieving forecast");
    }

    public String getForecast(final Update update) {
        Optional<UserEntity> user = userRepo.findById(update.getCallbackQuery().getFrom().getId());
        if (user.isEmpty()) return NEW_USER_RESPONSE;

        return user.map(getForecastByUserSettings())
                .map(YandexForecastMapper::mapToYandexModel)
                .map(YandexForecastModel::toRuStringResponse)
                .orElse("Error during retrieving forecast");
    }

    public SendMessage handleScheduledNotificationCreatingPipeline(final Update update) {
        // FIXME: 19.07.21 Possible multithreading issues
        if (isStarted(update)) {
            scheduledNotification = new ScheduledNotification();
            Optional<UserEntity> user = userRepo.findById(update.getCallbackQuery().getFrom().getId());
            if (user.isEmpty()) {
                return new SendMessage(update.getCallbackQuery().getMessage().getChatId().toString(),
                        "Пользователь не найден. Пришлите сначала свою геолокацию для записи пользователя");
            }
            scheduledNotification.setUser(UserMapper.mapToUser(user.get()));
            scheduledNotification.setChatId(update.getCallbackQuery().getMessage().getChatId().toString());
            return chooseHours(update);
        }
        if (areHoursSet(update)) {
            scheduledNotification.setNotificationTime(getPayloadFromUpdate(update));
            return chooseMinutes(update);
        }
        if (areMinutesSet(update)) {
            scheduledNotification.setNotificationTime(
                    scheduledNotification.getNotificationTime() + ":" + getPayloadFromUpdate(update));
            return chooseForecastType(update);
        }
        if (isForecastTypeSet(update)) {
            scheduledNotification.setForecastType(getPayloadFromUpdate(update));
            return chooseForecastSources(update);
        }
        if (isForecastSourceSet(update)) {
            if (scheduledNotification.getSources() == null) {
                scheduledNotification.setSources(getPayloadFromUpdate(update));
            } else {
                scheduledNotification.setSources(scheduledNotification.getSources() + "," + getPayloadFromUpdate(update));
            }
            return chooseForecastSources(update);
        }
        if (isFinished(update)) {
            ScheduledNotificationEntity scheduledNotificationEntity = mapToScheduledNotificationEntity(scheduledNotification);
            scheduledNotificationService.save(scheduledNotificationEntity);
            return new SendMessage(update.getCallbackQuery().getMessage().getChatId().toString(), "Уведомление создано");
        }

        return new SendMessage(update.getCallbackQuery().getMessage().getChatId().toString(), "Неизвестная команда");
    }

    private boolean isStarted(final Update update) {
        return isEventHappened(update, STARTED);
    }

    private SendMessage chooseHours(final Update update) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        IntStream.range(0, 25).forEach(num -> {
            var keyboardButton = new InlineKeyboardButton(String.valueOf(num));
            keyboardButton.setCallbackData(buildPipelineMessage(HOURS_SET, String.valueOf(num)));
            keyboard.add(List.of(keyboardButton));
        });

        var keyboardMarkup = new InlineKeyboardMarkup();
        keyboardMarkup.setKeyboard(keyboard);

        var message = new SendMessage();
        message.setChatId(update.getCallbackQuery().getMessage().getChatId().toString());
        message.setText("Выберите время. Часы");
        message.setReplyMarkup(keyboardMarkup);

        return message;
    }

    private boolean areHoursSet(final Update update) {
        return isEventHappened(update, HOURS_SET);
    }

    private SendMessage chooseMinutes(final Update update) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        IntStream.iterate(0, i -> i <= 60, i -> i + 5)
                .forEach(num -> {
                    var keyboardButton = new InlineKeyboardButton(String.valueOf(num));
                    keyboardButton.setCallbackData(buildPipelineMessage(MINUTES_SET, String.valueOf(num)));
                    keyboard.add(List.of(keyboardButton));
                });

        var keyboardMarkup = new InlineKeyboardMarkup();
        keyboardMarkup.setKeyboard(keyboard);

        var message = new SendMessage();
        message.setChatId(update.getCallbackQuery().getMessage().getChatId().toString());
        message.setText("Минуты");
        message.setReplyMarkup(keyboardMarkup);

        return message;
    }

    private boolean areMinutesSet(final Update update) {
        return isEventHappened(update, MINUTES_SET);
    }

    private SendMessage chooseForecastType(final Update update) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        Arrays.stream(ForecastType.values())
                .forEach(type -> {
                    var keyboardButton = new InlineKeyboardButton(type.value);
                    keyboardButton.setCallbackData(buildPipelineMessage(FORECAST_TYPE_SET, type.name()));
                    keyboard.add(List.of(keyboardButton));
                });

        var keyboardMarkup = new InlineKeyboardMarkup();
        keyboardMarkup.setKeyboard(keyboard);

        var message = new SendMessage();
        message.setChatId(update.getCallbackQuery().getMessage().getChatId().toString());
        message.setText("Выберите вид прогноза.");
        message.setReplyMarkup(keyboardMarkup);

        return message;
    }

    private boolean isForecastTypeSet(final Update update) {
        return isEventHappened(update, FORECAST_TYPE_SET);
    }

    private SendMessage chooseForecastSources(final Update update) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        Arrays.stream(ForecastSource.values())
                .forEach(type -> {
                    var keyboardButton = new InlineKeyboardButton(type.value);
                    keyboardButton.setCallbackData(buildPipelineMessage(FORECAST_SOURCE_SET, type.name()));
                    keyboard.add(List.of(keyboardButton));
                });

        var finishButton = new InlineKeyboardButton("Подтвердить и сохранить");
        finishButton.setCallbackData(buildPipelineMessage(FINISHED));
        keyboard.add(List.of(finishButton));

        var keyboardMarkup = new InlineKeyboardMarkup();
        keyboardMarkup.setKeyboard(keyboard);

        var message = new SendMessage();
        message.setChatId(update.getCallbackQuery().getMessage().getChatId().toString());
        message.setText("Выберите источники информации о погоде." +
                " Можно выбрать несколько последовательным нажатием на несколько вариантов. " +
                "Нажмите \"Подтвердить и сохранить\" для сохранения результатов выбор");
        message.setReplyMarkup(keyboardMarkup);

        return message;
    }

    private boolean isForecastSourceSet(final Update update) {
        return isEventHappened(update, FORECAST_SOURCE_SET);
    }

    private boolean isFinished(final Update update) {
        return isEventHappened(update, FINISHED);
    }

    private Function<UserEntity, YandexForecast> getForecastByUserSettings() {
        return userEntity -> {
            var lat = userEntity.getForecastLocation().getLat();
            var lon = userEntity.getForecastLocation().getLon();
            log.info("Returning forecast by params: lat = {}, lon = {}", lat, lon);
            return yandexForecastService.getForecast(UserMapper.mapToUser(userEntity));
        };
    }

    private Supplier<? extends Optional<? extends UserEntity>> createUser(final User user) {
        return () -> Optional.of(new UserEntity(user.getId(), "GMT+3"));
    }

    private String buildPipelineMessage(final ScheduledNotificationCreatingPipeline event) {
        return buildPipelineMessage(event, "");
    }

    private String buildPipelineMessage(final ScheduledNotificationCreatingPipeline event, final String stageData) {
        return String.format("%s;%s;%s", SCHEDULING_NOTIFICATION_CREATING.name(), event.name(), stageData);
    }

    private boolean isEventHappened(final Update update, final ScheduledNotificationCreatingPipeline event) {
        return update.getCallbackQuery().getData().split(";")[1].equals(event.name());
    }

    private String getPayloadFromUpdate(final Update update) {
        return update.getCallbackQuery().getData().split(";")[2];
    }

    public void deleteNotification(final Update update) {
        scheduledNotificationService.delete(update.getCallbackQuery().getMessage().getChatId().toString());
    }
}
