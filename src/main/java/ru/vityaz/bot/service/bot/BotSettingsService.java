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
            sendMeButton.setText("To subscribe from send click here");
        }
        sendMeButton.setCallbackData("SENDME_BUTTON");
        return sendMeButton;
    }

    public String changeSendMeSetting(Long chatId) {
        if (userService.isSubscribedToSend(chatId)) {
            auditService.logChanges("User +" + chatId + " unsubscribed from send command");
            userService.switchIsSubscribed(chatId);
            return "You have successfully unsubscribed!";
        } else {
            auditService.logChanges("User +" + chatId + " subscribed to send command");
            userService.switchIsSubscribed(chatId);
            return "You have successfully subscribed!";
        }

    }
}
