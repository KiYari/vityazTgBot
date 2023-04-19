package ru.vityaz.bot.config;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.vityaz.bot.service.VityazBot;

@Component
public class BotInitializer {
    @Autowired
    final BotConfig config;
    @Autowired
    VityazBot vityaz;

    public BotInitializer(BotConfig config) {
        this.config = config;
    }

    @EventListener({ContextRefreshedEvent.class})
    public void init() throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        try {
            telegramBotsApi.registerBot(vityaz);
        } catch (TelegramApiException e) {
            System.out.println(e.getMessage());
        }
    }
}
