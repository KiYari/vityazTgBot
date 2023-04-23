package ru.vityaz.bot.service.bot;

import com.vdurmont.emoji.EmojiParser;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.vityaz.bot.config.BotConfig;
import ru.vityaz.bot.service.AuditService;

import java.util.ArrayList;
import java.util.List;

@Service
public class VityazBot extends TelegramLongPollingBot {
    private final BotConfig config;
    private final AuditService auditService;
    private final BotMenuService botMenuService;

    public VityazBot(BotConfig config, AuditService auditService, BotMenuService botMenuService) {
        this.config = config;
        this.auditService = auditService;
        this.botMenuService = botMenuService;
        List<BotCommand> commandList = new ArrayList<>();
        commandList.add(new BotCommand("/start", "Invoke start command"));
        commandList.add(new BotCommand("/data", "get stored your stored"));
        commandList.add(new BotCommand("/cleanData", "deletes your stored data"));
        commandList.add(new BotCommand("/help", "info about bot commands"));
        commandList.add(new BotCommand("/settings", "set your prefrences"));
        try {
            this.execute(new SetMyCommands(commandList, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            auditService.logChanges(e, "Command menu exception");
        }

    }
    static final String HELPTEXT = """
            This bot is created in order to recognize thing in the picture.
            
            COMMANDS:
            '/start' - start a conversation with bot.
            '/data' - exposes stored data about user.
            '/cleandata' - permanently deletes stored data about user.
            '/help' - outputs help text
            '/settings' - lets user configure bot
            """;

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

            switch (messageText.toLowerCase()) {
                case "/start" -> sendMessage(chatId, botMenuService.start(message));
                case "/data" -> sendMessage(chatId, botMenuService.data(message));
                case "/cleandata" -> sendMessage(chatId, "Are you sure?", botMenuService.cleanDataConfirmation(message));
                case "/help" -> sendMessage(chatId, HELPTEXT);
                default -> sendMessage(chatId, "Unknown command, try using /help for command list");
            }
        } else if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
            Long chatId = update.getCallbackQuery().getMessage().getChatId();

            switch (callbackData.toUpperCase()) {
                case "YES_BUTTON" -> editMessage(chatId, botMenuService.cleanDataYes(chatId), messageId);
                case "NO_BUTTON" -> editMessage(chatId, botMenuService.cleanDataNo(chatId), messageId);
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
}
