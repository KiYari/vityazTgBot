package ru.vityaz.bot.config;

import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.vityaz.bot.service.AuditService;
import ru.vityaz.bot.service.bot.VityazBot;

@Component
public class BotInitializer {
    final BotConfig config;
    private final VityazBot vityaz;
    private final AuditService auditService;

    public BotInitializer(BotConfig config, VityazBot vityaz, AuditService auditService) {
        this.config = config;
        this.vityaz = vityaz;
        this.auditService = auditService;
    }

    @EventListener({ContextRefreshedEvent.class})
    public void init() throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        try {
            telegramBotsApi.registerBot(vityaz);
        } catch (TelegramApiException e) {
            auditService.logChanges(e);
        }
    }
}
