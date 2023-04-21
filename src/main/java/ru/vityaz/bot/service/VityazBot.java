package ru.vityaz.bot.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.vityaz.bot.config.BotConfig;

import java.util.ArrayList;
import java.util.List;

@Service
public class VityazBot extends TelegramLongPollingBot {
    private final BotConfig config;
    private final AuditService auditService;

    public VityazBot(BotConfig config, AuditService auditService) {
        this.config = config;
        this.auditService = auditService;
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
            String messageText = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();
            switch (messageText) {
                case "/start":
                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    break;
                case "/help":
                    sendMessage(chatId, HELPTEXT);
                    break;
                default:
                    sendMessage(chatId, "For now the only command is /start.");
            }
        }
    }

    private void startCommandReceived(Long chatId, String name) {
        String answer = "Hello, " + name + "!";
        auditService.logChanges("replied to user " + name + " text: " + answer);

        sendMessage(chatId, answer);
    }

    private void sendMessage(Long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(textToSend);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            auditService.logChanges(e);
        }
    }
}
