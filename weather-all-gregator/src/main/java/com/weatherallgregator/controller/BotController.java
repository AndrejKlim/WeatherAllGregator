package com.weatherallgregator.controller;

import com.weatherallgregator.dto.ForecastInfo;
import com.weatherallgregator.dto.ScheduledNotification;
import com.weatherallgregator.dto.WeatherInfo;
import com.weatherallgregator.enums.ForecastSource;
import com.weatherallgregator.enums.ForecastType;
import com.weatherallgregator.enums.ScheduledNotificationCreatingPipeline;
import com.weatherallgregator.jpa.entity.ForecastLocationEntity;
import com.weatherallgregator.jpa.entity.ScheduledNotificationEntity;
import com.weatherallgregator.jpa.entity.UserEntity;
import com.weatherallgregator.jpa.repo.UserRepo;
import com.weatherallgregator.service.ForecastLocationService;
import com.weatherallgregator.service.PressurePlotService;
import com.weatherallgregator.service.forecast.ForecastService;
import com.weatherallgregator.service.ScheduledNotificationService;
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

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import static com.weatherallgregator.enums.ScheduledNotificationCreatingPipeline.*;
import static com.weatherallgregator.mapper.ScheduledNotificationMapper.mapToScheduledNotificationEntity;
import static com.weatherallgregator.mapper.UserMapper.mapToUser;

@Slf4j
@Component
@RequiredArgsConstructor
public class BotController {

    public static final String NEW_USER_RESPONSE = "???? ???????????? ?????? ?????????????????????? ??????????????." +
            " ?????????????????? ???????? ???????????????????? ?????? ?????????????????? ????????????????.";
    private final List<ForecastService> forecastServiceList;
    private final ForecastLocationService forecastLocationService;
    private final ScheduledNotificationService scheduledNotificationService;
    private final PressurePlotService plotService;
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

    public List<String> getWeather(final Update update) {

        Optional<UserEntity> user = userRepo.findById(update.getCallbackQuery().getFrom().getId());
        if (user.isEmpty()) return List.of(NEW_USER_RESPONSE);

        return user
                .map(u -> forecastServiceList.stream()
                        .map(fs -> fs.getWeather(mapToUser(u)))
                        .map(WeatherInfo::toRuWeatherResponse)
                        .toList())
                .orElse(List.of("Error during retrieving forecast"));
    }

    public List<String> getForecast(final Update update) {
        Optional<UserEntity> user = userRepo.findById(update.getCallbackQuery().getFrom().getId());
        if (user.isEmpty()) return List.of(NEW_USER_RESPONSE);


        return user
                .map(u -> forecastServiceList.stream()
                        .map(fs -> fs.getForecast(mapToUser(u)))
                        .map(ForecastInfo::toRuForecastResponse)
                        .flatMap(List::stream)
                        .toList())
                .orElse(List.of("Error during retrieving forecast"));
    }

    public SendMessage handleScheduledNotificationCreatingPipeline(final Update update) {
        // FIXME: 19.07.21 Possible multithreading issues
        if (isStarted(update)) {
            scheduledNotification = new ScheduledNotification();
            Optional<UserEntity> user = userRepo.findById(update.getCallbackQuery().getFrom().getId());
            if (user.isEmpty()) {
                return new SendMessage(update.getCallbackQuery().getMessage().getChatId().toString(),
                        "???????????????????????? ???? ????????????. ???????????????? ?????????????? ???????? ???????????????????? ?????? ???????????? ????????????????????????");
            }
            scheduledNotification.setUser(mapToUser(user.get()));
            scheduledNotification.setChatId(update.getCallbackQuery().getMessage().getChatId().toString());
            return chooseHours(update);
        }
        if (areHoursSet(update)) {
            String notificationTimeHours = addLeadingZeroToOneDigitInt(update);
            scheduledNotification.setNotificationTime(notificationTimeHours);

            return chooseMinutes(update);
        }
        if (areMinutesSet(update)) {
            String notificationTimeMinutes = addLeadingZeroToOneDigitInt(update);
            scheduledNotification.setNotificationTime(
                    scheduledNotification.getNotificationTime() + ":" + notificationTimeMinutes);
            return chooseForecastType(update);
        }
        if (isForecastTypeSet(update)) {
            scheduledNotification.setForecastType(getPayloadFromUpdate(update));
            return chooseForecastSources(update);
        }
        if (isForecastSourceSet(update)) {
            if (getPayloadFromUpdate(update) == null){
                scheduledNotification.setSources(ForecastSource.ALL.name());
            }

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
            return new SendMessage(update.getCallbackQuery().getMessage().getChatId().toString(), "?????????????????????? ??????????????");
        }

        return new SendMessage(update.getCallbackQuery().getMessage().getChatId().toString(), "?????????????????????? ??????????????");
    }

    public void deleteNotification(final Update update) {
        scheduledNotificationService.delete(update.getCallbackQuery().getMessage().getChatId().toString());
    }

    private boolean isStarted(final Update update) {
        return isEventHappened(update, STARTED);
    }

    private SendMessage chooseHours(final Update update) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        IntStream.range(0, 24).forEach(num -> {
            var keyboardButton = new InlineKeyboardButton(String.valueOf(num));
            keyboardButton.setCallbackData(buildPipelineMessage(HOURS_SET, String.valueOf(num)));
            keyboard.add(List.of(keyboardButton));
        }); // FIXME: 2.08.21 maybe put buttons in rows

        var keyboardMarkup = new InlineKeyboardMarkup();
        keyboardMarkup.setKeyboard(keyboard);

        var message = new SendMessage();
        message.setChatId(update.getCallbackQuery().getMessage().getChatId().toString());
        message.setText("???????????????? ??????????. ????????");
        message.setReplyMarkup(keyboardMarkup);

        return message;
    }

    private boolean areHoursSet(final Update update) {
        return isEventHappened(update, HOURS_SET);
    }

    private SendMessage chooseMinutes(final Update update) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        IntStream.iterate(0, i -> i <= 55, i -> i + 5)
                .forEach(num -> {
                    var keyboardButton = new InlineKeyboardButton(String.valueOf(num));
                    keyboardButton.setCallbackData(buildPipelineMessage(MINUTES_SET, String.valueOf(num)));
                    keyboard.add(List.of(keyboardButton));
                }); // FIXME: 2.08.21 may be put buttons in rows

        var keyboardMarkup = new InlineKeyboardMarkup();
        keyboardMarkup.setKeyboard(keyboard);

        var message = new SendMessage();
        message.setChatId(update.getCallbackQuery().getMessage().getChatId().toString());
        message.setText("????????????");
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
        message.setText("???????????????? ?????? ????????????????.");
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

        var finishButton = new InlineKeyboardButton("?????????????????????? ?? ??????????????????");
        finishButton.setCallbackData(buildPipelineMessage(FINISHED));
        keyboard.add(List.of(finishButton));

        var keyboardMarkup = new InlineKeyboardMarkup();
        keyboardMarkup.setKeyboard(keyboard);

        var message = new SendMessage();
        message.setChatId(update.getCallbackQuery().getMessage().getChatId().toString());
        message.setText("???????????????? ?????????????????? ???????????????????? ?? ????????????." +
                " ?????????? ?????????????? ?????????????????? ???????????????????????????????? ???????????????? ???? ?????????????????? ??????????????????. " +
                "?????????????? \"?????????????????????? ?? ??????????????????\" ?????? ???????????????????? ?????????????????????? ??????????");
        message.setReplyMarkup(keyboardMarkup);

        return message;
    }

    private boolean isForecastSourceSet(final Update update) {
        return isEventHappened(update, FORECAST_SOURCE_SET);
    }

    private boolean isFinished(final Update update) {
        return isEventHappened(update, FINISHED);
    }

    private Supplier<? extends Optional<? extends UserEntity>> createUser(final User user) {
        return () -> Optional.of(new UserEntity(user.getId(), "GMT+3"));
    }

    private String buildPipelineMessage(final ScheduledNotificationCreatingPipeline event) {
        return buildPipelineMessage(event, "");
    }

    private String buildPipelineMessage(final ScheduledNotificationCreatingPipeline event, final String stageData) {
        return String.format("%s;%s;%s", SN_PIPELINE.name(), event.name(), stageData);
    }

    private boolean isEventHappened(final Update update, final ScheduledNotificationCreatingPipeline event) {
        return update.getCallbackQuery().getData().split(";")[1].equals(event.name());
    }

    private String getPayloadFromUpdate(final Update update) {
        return update.getCallbackQuery().getData().split(";")[2];
    }

    private String addLeadingZeroToOneDigitInt(final Update update) {
        String notificationTimeHours = getPayloadFromUpdate(update);
        if (notificationTimeHours.length() == 1) {
            notificationTimeHours = "0" + notificationTimeHours;
        }
        return notificationTimeHours;
    }

    public InputStream getHumidityPlot(final Update update) {
        return plotService.pressurePlot();
    }
}
