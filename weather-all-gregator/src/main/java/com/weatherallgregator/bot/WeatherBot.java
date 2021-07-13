package com.weatherallgregator.bot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weatherallgregator.controller.BotController;
import com.weatherallgregator.property.BotProperties;
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
    private final BotController botController;

    public WeatherBot(BotProperties botProperties, BotController botController) {
        super(botProperties.getUsername(), botProperties.getToken());
        this.botProperties = botProperties;
        this.botController = botController;
    }

    @Override
    public long creatorId() {
        return 381058662;
    }

    public Ability forecast() {
        return Ability.builder()
                .name("forecast")
                .locality(Locality.ALL)
                .privacy(Privacy.PUBLIC)
                .action(mc -> getForecast(mc))
                .build();
    }

    public Ability current() {
        return Ability.builder()
                .name("current")
                .locality(Locality.ALL)
                .privacy(Privacy.PUBLIC)
                .action(mc -> getCurrent(mc))
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
        String forecast = botController.getForecast();
        SendMessage message = new SendMessage(mc.chatId().toString(), forecast);
        silent.execute(message);
    }

    private void getCurrent(MessageContext mc) {
        String forecast = botController.getFactForecast();
        SendMessage message = new SendMessage(mc.chatId().toString(), forecast);
        message.enableMarkdownV2(true);
        silent.execute(message);
    }
}
