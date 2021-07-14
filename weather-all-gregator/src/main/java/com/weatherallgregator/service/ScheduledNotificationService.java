package com.weatherallgregator.service;

import com.weatherallgregator.bot.WeatherBot;
import com.weatherallgregator.dto.User;
import com.weatherallgregator.dto.YandexForecast;
import com.weatherallgregator.jpa.repo.ScheduledNotificationRepo;
import com.weatherallgregator.mapper.YandexForecastMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static com.weatherallgregator.mapper.UserMapper.mapToUser;

@Component
public class ScheduledNotificationService {

    private static final long DAY_MILLISECONDS = 86400000;

    private final SilentSender sender;
    private final ScheduledNotificationRepo scheduledNotificationRepo;
    private final YandexForecastService yandexForecastService;

    private final Timer timer = new Timer();
    private final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

    public ScheduledNotificationService(WeatherBot weatherBot, ScheduledNotificationRepo scheduledNotificationRepo, YandexForecastService yandexForecastService) {
        this.sender = weatherBot.silent(); // TODO check another way to send message
        this.scheduledNotificationRepo = scheduledNotificationRepo;
        this.yandexForecastService = yandexForecastService;
    }

    @PostConstruct
    public void initScheduledTasks(){
        var scheduledNotifications = scheduledNotificationRepo.findAll();
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
    private class ScheduledNotificationTimerTask extends TimerTask{

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
