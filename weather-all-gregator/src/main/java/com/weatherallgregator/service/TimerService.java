package com.weatherallgregator.service;

import com.weatherallgregator.bot.WeatherBot;
import com.weatherallgregator.dto.User;
import com.weatherallgregator.dto.yandex.YandexForecast;
import com.weatherallgregator.jpa.repo.ScheduledNotificationRepo;
import com.weatherallgregator.mapper.YandexForecastMapper;
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
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import static com.weatherallgregator.mapper.UserMapper.mapToUser;

@Component
public class TimerService {

    private static final long DAY_MILLISECONDS = 86400000;

    private final SilentSender sender;
    private final ScheduledNotificationRepo scheduledNotificationRepo;
    private final YandexForecastService yandexForecastService;

    private Timer timer = new Timer();
    private int taskListSize = 0;

    public TimerService(WeatherBot weatherBot, ScheduledNotificationRepo scheduledNotificationRepo, YandexForecastService yandexForecastService) {
        this.sender = weatherBot.silent(); // TODO check another way to send message
        this.scheduledNotificationRepo = scheduledNotificationRepo;
        this.yandexForecastService = yandexForecastService;
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

                    return new ScheduledNotificationTimerTask(chatId, executionTime, mapToUser(notification.getUser()));
                })
                .forEach(task -> timer.schedule(task, task.getExecutionTime(), DAY_MILLISECONDS));
    }

    @Getter
    @Setter
    @AllArgsConstructor
    private class ScheduledNotificationTimerTask extends TimerTask {

        private String chatId;
        private Date executionTime;
        private User user;

        @Override
        public void run() {
            YandexForecast forecast = yandexForecastService.getForecast(user);
            String messageText = YandexForecastMapper.mapToFactYandexModel(forecast).toRuStringResponse();
            sender.execute(new SendMessage(chatId, messageText));
        }
    }
}
