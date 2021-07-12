package com.weatherallgregator.config;

import com.weatherallgregator.property.BotProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.meta.generics.LongPollingBot;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Configuration
@EnableConfigurationProperties(BotProperties.class)
public class BotConfig {

    private List<BotSession> botSessions = new ArrayList<>();
    private final List<LongPollingBot> pollingBots;

    public BotConfig(List<LongPollingBot> pollingBots) {
        this.pollingBots = pollingBots;
    }


    @PostConstruct
    public void start() {
        log.info("Starting auto config for telegram bots");
        TelegramBotsApi api = telegramBotsApi();
        pollingBots.forEach(bot -> {
            try {
                log.info("Registering polling bot: {}", bot.getBotUsername());
                botSessions.add(api.registerBot(bot));
            } catch (TelegramApiException e) {
                log.error("Failed to register bot {} due to error", bot.getBotUsername(), e);
            }
        });
    }

    @Bean
    public TelegramBotsApi telegramBotsApi() {
        return new TelegramBotsApi();
    }

    @PreDestroy
    public void stop() {
        botSessions.forEach(session -> {
            if (session != null) {
                session.stop();
            }
        });
    }
}
