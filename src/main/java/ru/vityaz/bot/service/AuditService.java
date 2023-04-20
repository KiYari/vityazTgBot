package ru.vityaz.bot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuditService {
    public void logChanges(Exception e) {
        log.error("Error occured: " + e.getMessage());
    }

    public void logChanges(Exception e, String msg) {
        log.error("Error occured: " + e.getMessage() + "\nAdditional messaage: " + msg);
    }

    public void logChanges(String msg, Exception e) {
        log.error("Error occured: " + e.getMessage() + "\nAdditional messaage: " + msg);
    }

    public void logChanges(String msg) {
        log.info(msg);
    }
}
