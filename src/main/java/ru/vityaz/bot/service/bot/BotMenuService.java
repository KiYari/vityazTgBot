package ru.vityaz.bot.service.bot;

import com.vdurmont.emoji.EmojiParser;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.vityaz.bot.service.AuditService;
import ru.vityaz.bot.service.UserService;

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
        String answer = EmojiParser.parseToUnicode("Hello, " + name + "! It's pleasure to meet you! :blush:");
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

    public String cleanData(Message message) {
        var chatId = message.getChatId();

        if(userService.isUserInDatabase(chatId)) {
            auditService.logChanges("User " + chatId + "deleted stored data about himself:\n" + userService.getUserStoredData(chatId).toString());
            userService.delete(chatId);
            return "Your data deleted successfully!";
        }
        auditService.logChanges("User + " + chatId + " tried to delete his data, but nothing stored");
        return "No data stored about you";
    }
}
