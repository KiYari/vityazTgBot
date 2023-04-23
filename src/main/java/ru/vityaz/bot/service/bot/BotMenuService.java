package ru.vityaz.bot.service.bot;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.vityaz.bot.service.AuditService;
import ru.vityaz.bot.service.UserService;

import java.util.ArrayList;
import java.util.List;

@Service
public class BotMenuService {
    private final UserService userService;
    private final AuditService auditService;

    public BotMenuService(UserService userService, AuditService auditService) {
        this.userService = userService;
        this.auditService = auditService;
    }

    public String start(Message message) {
        var name = message.getChat().getFirstName();
        userService.save(message);
        String answer = "Hello, " + name + "! It's pleasure to meet you! :blush:";
        auditService.logChanges("replied to user " + name + " text: " + answer);
        return answer;
    }

    public String data(Message message) {
        var chatId = message.getChatId();

        if (userService.isUserInDatabase(chatId)) {
            auditService.logChanges("User + " + chatId + " got his data");
            return userService.getUserStoredData(chatId).toString();
        }
        auditService.logChanges("User + " + chatId + " tried to get his data, but nothing stored");
        return "No data stored about you";
    }

    public InlineKeyboardMarkup cleanDataConfirmation(Message message) {
        var chatId = message.getChatId();
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
}
