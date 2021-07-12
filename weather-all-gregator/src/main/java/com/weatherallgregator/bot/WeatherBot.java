package com.weatherallgregator.bot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weatherallgregator.dto.YandexForecast;
import com.weatherallgregator.property.BotProperties;
import com.weatherallgregator.service.YandexForecastService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.objects.Locality;
import org.telegram.abilitybots.api.objects.MessageContext;
import org.telegram.abilitybots.api.objects.Privacy;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Component
@Slf4j
public class WeatherBot extends AbilityBot {

    private final BotProperties botProperties;
    private final YandexForecastService forecastService;
    private final ObjectMapper objectMapper;

    public WeatherBot(BotProperties botProperties, YandexForecastService forecastService, ObjectMapper objectMapper) {
        super(botProperties.getUsername(), botProperties.getToken());
        this.botProperties = botProperties;
        this.forecastService = forecastService;
        this.objectMapper = objectMapper;
    }

    @Override
    public long creatorId() {
        return 381058662;
    }

    public Ability forecast(){
        return Ability.builder()
                .name("forecast")
                .locality(Locality.ALL)
                .privacy(Privacy.PUBLIC)
                .action(mc -> getForecast(mc))
                .build();
    }

    @Override
    public String getBotToken() {
        return botProperties.getToken();
    }

    @Override
    public String getBotUsername() {
        return botProperties.getUsername();
    }

    private void getForecast(MessageContext mc) {
        YandexForecast forecast = forecastService.getForecast("53.6884", "23.8258");
        SendMessage message = null;
        try {
            message = new SendMessage(mc.chatId().toString(), objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(forecast));
        } catch (JsonProcessingException e) {
            log.error("Error during writing to json. Message = {}", e.getMessage());
        }
        silent.execute(message);
    }
}
