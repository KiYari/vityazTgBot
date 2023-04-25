package ru.vityaz.bot.service.bot;

import com.vdurmont.emoji.EmojiParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.vityaz.bot.config.BotConfig;
import ru.vityaz.bot.service.AuditService;
import ru.vityaz.bot.service.UserService;
import ru.vityaz.bot.service.external.weather.WeatherService;
import ru.vityaz.bot.util.exception.MarkupNullException;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class VityazBot extends TelegramLongPollingBot {
    private final BotConfig config;
    private final AuditService auditService;
    private final BotMenuService botMenuService;
    private final UserService userService;
    private final BotSettingsService botSettingsService;
    private final WeatherService weatherService;

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            String messageText = message.getText();
            Long chatId = message.getChatId();

            if(messageText.startsWith("/send")) {
                if (userService.isAdmin(chatId)) {
                    userService.getAllUserIds().stream()
                            .filter(userService::isSubscribedToSend)
                            .forEach(id -> sendMessage(id, botMenuService.send(id, messageText)));
                } else {
                    sendMessage(chatId, "You are not allowed to this command.");
                }
            } else {
                switch (messageText.toLowerCase()) {
                    case "/start" -> sendMessage(chatId, botMenuService.start(message));
                    case "/data" -> sendMessage(chatId, botMenuService.data(chatId));
                    case "/cleandata" -> sendMessage(chatId, "Are you sure?", botMenuService.cleanDataConfirmation(chatId));
                    case "/help" -> sendMessage(chatId, botMenuService.help(chatId));
                    case "/settings" -> sendMessage(chatId, "Available options below", botMenuService.settings(chatId));
                    case "/showmeweather" -> sendMessage(chatId, weatherService.getWeather("Vladivostok", new Date()));
                    default -> sendDefaultMessage(chatId, "Unknown command, try using /help for command list", messageText);
                }
            }
        } else if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
            Long chatId = update.getCallbackQuery().getMessage().getChatId();

            switch (callbackData.toUpperCase()) {
                case "YES_BUTTON" -> editMessage(chatId, botMenuService.cleanDataYes(chatId), messageId);
                case "NO_BUTTON" -> editMessage(chatId, botMenuService.cleanDataNo(chatId), messageId);
                case "SENDME_BUTTON" -> editMessage(chatId, botSettingsService.changeSendMeSetting(chatId) , messageId);
                default -> editMessage(chatId, "I'm sorry, try again later. Command went incorrect.", messageId);
            }
        }
    }

    private void sendMessage(Long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(EmojiParser.parseToUnicode(textToSend));
        try {
            execute(message);
        } catch (TelegramApiException e) {
            auditService.logChanges(e);
        }
    }

    public void sendMessage(Long chatId, String textToSend, InlineKeyboardMarkup markup) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        if (markup == null) {
            message.setText("Something went wrote, please, try again and consider give us log file.");
            auditService.logChanges(new MarkupNullException(), "InlineKeyboardMarkup == null when sending message" );
        } else {
            message.setText(EmojiParser.parseToUnicode(textToSend));
            message.setReplyMarkup(markup);
        }
        try {
            execute(message);
        } catch (TelegramApiException e) {
            auditService.logChanges(e);
        }
    }

    public void editMessage(Long chatId, String textToChange, Integer messageId) {
        EditMessageText message = new EditMessageText();
        message.setChatId(chatId);
        message.setText(textToChange);
        message.setMessageId(messageId);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            auditService.logChanges(e);
        }
    }

    private void sendDefaultMessage(Long chatId, String textToSend, String messageText) {
        auditService.logChanges("User " + chatId + " tried to use unknown command. He typed: " + messageText);
        sendMessage(chatId, textToSend);
    }
}
