package ru.vityaz.bot.service.bot;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.vityaz.bot.service.AuditService;
import ru.vityaz.bot.service.UserService;

@Service
public class BotSettingsService {
    private final UserService userService;
    private final AuditService auditService;

    public BotSettingsService(UserService userService, AuditService auditService) {
        this.userService = userService;
        this.auditService = auditService;
    }

    public InlineKeyboardButton subscribedToSendSettingButton(Long chatId) {
        var sendMeButton = new InlineKeyboardButton();
        if(userService.isSubscribedToSend(chatId)) {
            sendMeButton.setText("To unsubscribe from send click here");
        } else {
            sendMeButton.setText("To subscribe to send click here");
        }
        sendMeButton.setCallbackData("SENDME_BUTTON");
        return sendMeButton;
    }

    public String changeSendMeSetting(Long chatId) {
        if (userService.isSubscribedToSend(chatId)) {
            auditService.logChanges("User +" + chatId + " unsubscribed from send command");
            userService.switchIsSubscribed(chatId);
            return "You have successfully unsubscribed from general information sending!";
        } else {
            auditService.logChanges("User +" + chatId + " subscribed to send command");
            userService.switchIsSubscribed(chatId);
            return "You have successfully subscribed to general information sending!";
        }

    }

    public InlineKeyboardButton subscribedToWeatherAutosendSettingButton(Long chatId) {
        var weatheerButton = new InlineKeyboardButton();
        if(userService.isSubscribedToWeatherAutosend(chatId)) {
            weatheerButton.setText("To unsubscribe from weather autosending click here");
        } else {
            weatheerButton.setText("To subscribe to weather autosending click here");
        }
        weatheerButton.setCallbackData("AUTOSENDMEWEATHER_BUTTON");
        return weatheerButton;
    }

    public String changeWeatherAutosendSetting(Long chatId) {
        if (userService.isSubscribedToWeatherAutosend(chatId)) {
            auditService.logChanges("User +" + chatId + " unsubscribed from weather autosending command");
            userService.switchIsSubscribedToWeatherAutosend(chatId);
            return "You have successfully unsubscribed from weather auto sending!";
        } else {
            auditService.logChanges("User +" + chatId + " subscribed to weather autosending command");
            userService.switchIsSubscribedToWeatherAutosend(chatId);
            return "You have successfully subscribed to weather auto sending!";
        }

    }
}
