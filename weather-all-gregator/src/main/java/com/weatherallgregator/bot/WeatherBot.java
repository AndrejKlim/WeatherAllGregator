package com.weatherallgregator.bot;

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
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;

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

    public Ability menu() {
        return Ability.builder()
                .name("menu")
                .locality(Locality.ALL)
                .privacy(Privacy.PUBLIC)
                .action(mc -> {
                    KeyboardButton locationButton = new KeyboardButton();
                    locationButton.setText("Send my location to set as default");
                    locationButton.setRequestLocation(true);

                    KeyboardButton forecastButton = new KeyboardButton();
                    forecastButton.setText("/forecast");

                    KeyboardButton currentButton = new KeyboardButton();
                    currentButton.setText("/current");

                    ReplyKeyboardMarkup replyKeyboardMarkup = ReplyKeyboardMarkup.builder()
                            .keyboardRow(new KeyboardRow(List.of(locationButton)))
                            .keyboardRow(new KeyboardRow(List.of(forecastButton)))
                            .keyboardRow(new KeyboardRow(List.of(currentButton)))
                            .build();

                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setChatId(mc.chatId().toString());
                    sendMessage.setText("Menu");
                    sendMessage.setReplyMarkup(replyKeyboardMarkup);

                    silent.execute(sendMessage);
                })
                .build();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.getMessage().getLocation() != null){
            botController.saveLocation(update);
        }
        super.onUpdateReceived(update);
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
        String forecast = botController.getForecast(mc);
        SendMessage message = new SendMessage(mc.chatId().toString(), forecast);
        silent.execute(message);
    }

    private void getCurrent(MessageContext mc) {
        String forecast = botController.getFactForecast(mc);
        SendMessage message = new SendMessage(mc.chatId().toString(), forecast);
        silent.execute(message);
    }
}
