package com.weatherallgregator.bot;

import com.weatherallgregator.controller.BotController;
import com.weatherallgregator.enums.BotCommands;
import com.weatherallgregator.property.BotProperties;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.objects.Locality;
import org.telegram.abilitybots.api.objects.Privacy;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

import static com.weatherallgregator.enums.ScheduledNotificationCreatingPipeline.SN_PIPELINE;
import static com.weatherallgregator.enums.ScheduledNotificationCreatingPipeline.STARTED;

@Component
@Slf4j
public class WeatherBot extends AbilityBot {

    public static final String MARKDOWN = "Markdown";
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

    public Ability menu() {
        return Ability.builder()
                .name("menu")
                .locality(Locality.ALL)
                .privacy(Privacy.PUBLIC)
                .action(mc -> {
                    var locationButton = new InlineKeyboardButton("Установить локацию для прогноза");
                    locationButton.setCallbackData(BotCommands.SET_LOCATION.name());

                    var forecastButton = new InlineKeyboardButton("Ближайший прогноз");
                    forecastButton.setCallbackData(BotCommands.FORECAST.name());

                    var currentButton = new InlineKeyboardButton("Погода на настоящий момент");
                    currentButton.setCallbackData(BotCommands.WEATHER.name());

                    var scheduleButton = new InlineKeyboardButton("Запланировать уведомления о погоде");
                    scheduleButton.setCallbackData(String.format("%s;%s;%s",
                            SN_PIPELINE.name(),
                            STARTED.name(),
                            ""));

                    var deleteNotificationButton = new InlineKeyboardButton("Удалить уведомление");
                    deleteNotificationButton.setCallbackData(BotCommands.DELETE_NOTIFICATION.name());

                    List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
                    keyboard.add(List.of(currentButton));
                    keyboard.add(List.of(forecastButton));
                    keyboard.add(List.of(scheduleButton));
                    keyboard.add(List.of(deleteNotificationButton));
                    keyboard.add(List.of(locationButton));

                    var keyboardMarkup = new InlineKeyboardMarkup();
                    keyboardMarkup.setKeyboard(keyboard);

                    var sendMessage = new SendMessage(mc.chatId().toString(), "Меню команд");
                    sendMessage.setReplyMarkup(keyboardMarkup);

                    silent.execute(sendMessage);
                })
                .build();
    }

    @Override
    public void onUpdateReceived(final Update update) {
        if (update.hasMessage() && update.getMessage().getLocation() != null) {
            botController.saveLocation(update);
            return;
        }
        if (!update.hasCallbackQuery()) {
            super.onUpdateReceived(update);
            return;
        }

        if (isForecastRequested(update)) {
            getForecast(update);
        } // FIXME: 2.08.21 may be show menu dialog after weather and forecast messages
        if (isWeatherRequested(update)){
            getWeather(update);
        }
        if (isLocationSetRequested(update)){
            getLocationButton(update);
        }
        if (isDeletingNotificationRequested(update)){
            deleteNotification(update);
        }

        if (isScheduledNotificationCreatingPipeline(update)) {
            silent.execute(botController.handleScheduledNotificationCreatingPipeline(update));
        }
    }


    @Override
    public String getBotToken() {
        return botProperties.getToken();
    }

    @Override
    public String getBotUsername() {
        return botProperties.getUsername();
    }

    private boolean isForecastRequested(final Update update) {
        return checkBotCommand(update, BotCommands.FORECAST);
    }

    private boolean isWeatherRequested(final Update update) {
        return checkBotCommand(update, BotCommands.WEATHER);
    }

    private boolean isLocationSetRequested(final Update update) {
        return checkBotCommand(update, BotCommands.SET_LOCATION);
    }

    private boolean isDeletingNotificationRequested(final Update update) {
        return checkBotCommand(update, BotCommands.DELETE_NOTIFICATION);
    }

    private boolean isScheduledNotificationCreatingPipeline(Update update) {
        return update.getCallbackQuery().getData().startsWith(SN_PIPELINE.name());
    }

    private boolean checkBotCommand(final Update update, final BotCommands command) {
        return update.getCallbackQuery().getData().equals(command.name());
    }

    private void getForecast(final Update update) {
        botController.getForecast(update).stream()
                .map(s -> {
                    final var sendMessage = new SendMessage(getChatId(update), s);
                    sendMessage.setParseMode(MARKDOWN);
                    return sendMessage;
                })
                .forEach(message -> silent.execute(message));
    }

    private void getWeather(final Update update) {
        botController.getWeather(update).stream()
                .map(s -> {
                    final var sendMessage = new SendMessage(getChatId(update), s);
                    sendMessage.setParseMode(MARKDOWN);
                    return sendMessage;
                })
                .forEach(message -> silent.execute(message));
    }

    private void getLocationButton(final Update update) {

        var locationButton = new KeyboardButton("Отправить мою геолокацию и сохранить ее для получения прогноза.");
        locationButton.setRequestLocation(true);

        var keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setKeyboard(List.of(new KeyboardRow(List.of(locationButton))));

        var message = new SendMessage(getChatId(update), "Geolocation");
        message.setReplyMarkup(keyboardMarkup);

        silent.execute(message);
    }

    private void deleteNotification(final Update update) {
        botController.deleteNotification(update);
        silent.execute(new SendMessage(getChatId(update), "Удалено"));
    }

    @NotNull
    private String getChatId(final Update update) {
        return update.getCallbackQuery().getMessage().getChatId().toString();
    }
}
