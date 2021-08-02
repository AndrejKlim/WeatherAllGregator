package com.weatherallgregator.service;

import com.weatherallgregator.bot.WeatherBot;
import com.weatherallgregator.dto.ForecastInfo;
import com.weatherallgregator.dto.User;
import com.weatherallgregator.dto.WeatherInfo;
import com.weatherallgregator.enums.ForecastSource;
import com.weatherallgregator.enums.ForecastType;
import com.weatherallgregator.jpa.repo.ScheduledNotificationRepo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static com.weatherallgregator.mapper.UserMapper.mapToUser;

@Component
public class TimerService {

    private static final long DAY_MILLISECONDS = 86400000;

    private final SilentSender sender;
    private final ScheduledNotificationRepo scheduledNotificationRepo;
    private final List<ForecastService> forecastServiceList;

    private Timer timer = new Timer();
    private int taskListSize = 0;

    public TimerService(final WeatherBot weatherBot,
                        final ScheduledNotificationRepo scheduledNotificationRepo,
                        final List<ForecastService> forecastServiceList) {
        this.sender = weatherBot.silent(); // TODO check another way to send message
        this.scheduledNotificationRepo = scheduledNotificationRepo;
        this.forecastServiceList = forecastServiceList;
    }

    @PostConstruct
    public void initScheduledTasks() {
        fillTimerQueueFromDb();
    }

    @Scheduled(fixedRate = 60000)
    public void updateTimerTasks() {
        var actualNotificationCount = scheduledNotificationRepo.count();
        if (actualNotificationCount == taskListSize) {
            return;
        }

        timer.cancel();
        timer = new Timer();
        fillTimerQueueFromDb();
    }

    private void fillTimerQueueFromDb() {
        var scheduledNotifications = scheduledNotificationRepo.findAll();
        taskListSize = scheduledNotifications.size();
        scheduledNotifications.stream()
                .map(notification -> {

                    String chatId = notification.getChatId();
                    Date executionTime = Date.from(LocalDate.now()
                            .atTime(LocalTime.parse(notification.getNotificationTime()))
                            .atZone(ZoneId.of(notification.getUser().getTimeZone()))
                            .toInstant());
                    String forecastType = notification.getForecastType();
                    List<ForecastSource> sources = Arrays.stream(notification.getSources().split(","))
                            .map(ForecastSource::valueOf)
                            .collect(Collectors.toList());

                    return new ScheduledNotificationTimerTask( mapToUser(notification.getUser()), chatId, executionTime,
                            ForecastType.valueOfType(forecastType), sources);
                })
                .forEach(task -> timer.schedule(task, task.getExecutionTime(), DAY_MILLISECONDS));
    }

    @Getter
    @Setter
    @AllArgsConstructor
    private class ScheduledNotificationTimerTask extends TimerTask {

        private User user;
        private String chatId;
        private Date executionTime;
        private ForecastType forecastType;
        private List<ForecastSource> forecastSourceList;

        @Override
        public void run() {
            forecastServiceList.stream()
                    .filter(forecastService -> {
                        if (forecastSourceList.size() == 1 && forecastSourceList.get(0).equals(ForecastSource.ALL)){
                            return true;
                        } else {
                            return forecastSourceList.contains(forecastService.getSource());
                        }
                    })
                    .map(forecastService -> {
                        switch (forecastType) {
                            case FACT:
                                WeatherInfo weather = forecastService.getWeather(user);
                                return weather.toRuWeatherResponse();
                            case FORECAST:
                                ForecastInfo forecast = forecastService.getForecast(user);
                                return forecast.toRuForecastResponse();
                            default:
                                return "Unexpected forecast type, check logs";
                        }
                    })
                    .forEach(messageText -> {
                        final var sendMessage = new SendMessage(chatId, messageText);
                        sendMessage.setParseMode("Markdown");
                        sender.execute(sendMessage);
                    });
        }
    }
}
