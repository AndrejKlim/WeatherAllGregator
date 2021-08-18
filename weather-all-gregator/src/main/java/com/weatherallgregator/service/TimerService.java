package com.weatherallgregator.service;

import com.weatherallgregator.bot.WeatherBot;
import com.weatherallgregator.dto.ForecastInfo;
import com.weatherallgregator.dto.User;
import com.weatherallgregator.dto.WeatherInfo;
import com.weatherallgregator.enums.ForecastSource;
import com.weatherallgregator.enums.ForecastType;
import com.weatherallgregator.jpa.entity.ScheduledNotificationEntity;
import com.weatherallgregator.jpa.repo.ScheduledNotificationRepo;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.time.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.weatherallgregator.mapper.UserMapper.mapToUser;

@Component
public class TimerService {

    private final SilentSender sender;
    private final ScheduledNotificationRepo scheduledNotificationRepo;
    private final List<ForecastService> forecastServiceList;

    private List<ScheduledNotificationEntity> scheduledNotifications = new ArrayList<>();

    public TimerService(final WeatherBot weatherBot,
                        final ScheduledNotificationRepo scheduledNotificationRepo,
                        final List<ForecastService> forecastServiceList) {
        this.sender = weatherBot.silent(); // TODO check another way to send message
        this.scheduledNotificationRepo = scheduledNotificationRepo;
        this.forecastServiceList = forecastServiceList;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void resetExecutedFlag() {
        List<ScheduledNotificationEntity> notifications = scheduledNotificationRepo.findAll();
        for (ScheduledNotificationEntity notification : notifications) {
            if (notification.getExecuted()){
                notification.setExecuted(false);
            }
        }

        scheduledNotificationRepo.saveAll(notifications);
    }

    @Scheduled(fixedRate = 60000)
    public void runTasks() {
        fillNotificationsFromDb();

        scheduledNotifications.stream()
                .filter(TimerService::checkNotificationTime)
                .forEach(notification -> {

                    String chatId = notification.getChatId();
                    ForecastType forecastType = ForecastType.valueOfType(notification.getForecastType());
                    List<ForecastSource> sources = Arrays.stream(notification.getSources().split(","))
                            .map(ForecastSource::valueOf)
                            .collect(Collectors.toList());
                    User user = mapToUser(notification.getUser());

                    sendForecast(chatId, forecastType, sources, user);
                    notification.setExecuted(true);
                });

        scheduledNotificationRepo.saveAll(scheduledNotifications);
    }

    private void fillNotificationsFromDb() {
        scheduledNotifications = scheduledNotificationRepo.findAll();
    }

    private static boolean checkNotificationTime(ScheduledNotificationEntity notification) {
        if (notification.getExecuted()) {
            return false;
        }

        final String timeZone = notification.getUser().getTimeZone();
        final ZonedDateTime nowZoned = ZonedDateTime.now().withZoneSameInstant(ZoneId.of(timeZone));
        final LocalDateTime notificationTime = LocalDate.now().atTime(LocalTime.parse(notification.getNotificationTime()));
        final Duration duration = Duration.between(nowZoned.toLocalDateTime(), notificationTime);

        int secInMinute = 60;

        if (duration.isNegative()) {
            return duration.getSeconds() > -secInMinute;
        } else {
            return duration.getSeconds() < secInMinute;
        }
    }

    private void sendForecast(final String chatId, final ForecastType forecastType, final List<ForecastSource> sources, final User user) {
        forecastServiceList.stream()
                .filter(forecastService -> {
                    if (sources.size() == 1 && sources.get(0).equals(ForecastSource.ALL)) {
                        return true;
                    } else {
                        return sources.contains(forecastService.getSource());
                    }
                })
                .map(forecastService -> {
                    switch (forecastType) {
                        case WEATHER:
                            WeatherInfo weather = forecastService.getWeather(user);
                            return List.of(weather.toRuWeatherResponse());
                        case FORECAST:
                            ForecastInfo forecast = forecastService.getForecast(user);
                            return forecast.toRuForecastResponse();
                        default:
                            return List.of("Unexpected forecast type, check logs");
                    }
                })
                .flatMap(List::stream)
                .forEach(messageText -> {
                    final var sendMessage = new SendMessage(chatId, messageText);
                    sendMessage.setParseMode("Markdown");
                    sender.execute(sendMessage);
                });
    }
}