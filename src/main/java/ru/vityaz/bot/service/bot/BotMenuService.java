package ru.vityaz.bot.service.bot;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.vityaz.bot.model.entity.User;
import ru.vityaz.bot.service.AuditService;
import ru.vityaz.bot.service.UserService;
import ru.vityaz.bot.service.external.weather.WeatherService;
import ru.vityaz.bot.util.exception.NoCitySetException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class BotMenuService {
    private final UserService userService;
    private final AuditService auditService;
    private final BotSettingsService botSettingsService;
    static final String HELPTEXT = """
            This bot is created in order to recognize thing in the picture.
            
            COMMANDS:
            /start - start a conversation with bot.
            /data - exposes stored data about user.
            /cleanData - permanently deletes stored data about user.
            /help - outputs help text
            /settings - lets user configure bot
            /setCity - sets your city. Write your city after space e.g. /setCity Inta
            /showMeWeather - shows weather in your city
            """;
    static final String ADMINTEXT = """
            /send - sends messages to all subscribed users
            """;
    private final WeatherService weatherService;

    public BotMenuService(UserService userService, AuditService auditService, BotSettingsService botSettingsService, WeatherService weatherService) {
        this.userService = userService;
        this.auditService = auditService;
        this.botSettingsService = botSettingsService;
        this.weatherService = weatherService;
    }

    public String start(Message message) {
        var name = message.getChat().getFirstName();
        userService.save(message);
        String answer = "Hello, " + name + "! It's pleasure to meet you! :blush:";
        auditService.logChanges("replied to user " + name + " text: " + answer);
        return answer;
    }

    public String help(Long chatId) {
        if (userService.isAdmin(chatId)) {
            return HELPTEXT + ADMINTEXT;
        } else {
            return HELPTEXT;
        }
    }

    public String data(Long chatId) {
        if (userService.isUserInDatabase(chatId)) {
            auditService.logChanges("User + " + chatId + " got his data");
            return userService.getUserStoredData(chatId).toString();
        }
        auditService.logChanges("User + " + chatId + " tried to get his data, but nothing stored");
        return "No data stored about you";
    }

    public InlineKeyboardMarkup cleanDataConfirmation(Long chatId) {
        auditService.logChanges("User + " + chatId + " tried to delete his data");
        if(userService.isUserInDatabase(chatId)){
            InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rows = new ArrayList<>();
            List<InlineKeyboardButton> row1 = new ArrayList<>();

            var yesButton = new InlineKeyboardButton();
            yesButton.setText("Yes");
            yesButton.setCallbackData("YES_BUTTON");

            var noButton = new InlineKeyboardButton();
            noButton.setText("No");
            noButton.setCallbackData("NO_BUTTON");

            row1.add(yesButton);
            row1.add(noButton);
            rows.add(row1);
            markup.setKeyboard(rows);

            return markup;
        }
        return null;
    }

    public String cleanDataYes(Long chatId) {
        if(userService.isUserInDatabase(chatId)) {
            auditService.logChanges("User " + chatId + " deleted stored data about himself:\n" + userService.getUserStoredData(chatId).toString());
            userService.delete(chatId);
            return "Your data deleted successfully!";
        }
        auditService.logChanges("User + " + chatId + " tried to delete his data, but nothing stored");
        return "No data stored about you";
    }

    public String cleanDataNo(Long chatId) {
        auditService.logChanges("User + " + chatId + " changed his mind about deleting his data");
        return "Alright, your data will be saved!";
    }

    public String send(Long chatId, String textToSend) {
        if(userService.isSubscribedToSend(chatId)) {
            String message = textToSend.substring(6);
            auditService.logChanges("User + " + chatId + " send message: " + message + " to everyone");
            return message;
        } else {
            return null;
        }
    }

    public String showMeWeather(Long chatId) {
        if (userService.getUserCity(chatId) == null) {
            auditService.logChanges(new NoCitySetException(), "User " + chatId + "didnt get his weather due no city set");
            return "Please use /setcity command to set your city";
        } else {
            return weatherService.getWeather(userService.getUserCity(chatId), LocalDateTime.now(), chatId);
        }
    }

    public String setCity(Long chatId, String city) {
        User user = userService.getById(chatId);
        user.setCity(city);
        userService.save(user);
        return "Your city saved!";
    }

    public InlineKeyboardMarkup settings(Long chatId) {
        auditService.logChanges("User + " + chatId + " tried to access settings");
        if(userService.isUserInDatabase(chatId)){
            InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rows = new ArrayList<>();
            List<InlineKeyboardButton> row1 = new ArrayList<>();

            var sendMeButton = botSettingsService.subscribedToSendSettingButton(chatId);
            var weatherButton = botSettingsService.subscribedToWeatherAutosendSettingButton(chatId);

            row1.add(sendMeButton);
            row1.add(weatherButton);

            rows.add(row1);

            markup.setKeyboard(rows);

            return markup;
        }
        return null;
    }
}
