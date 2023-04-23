package ru.vityaz.bot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.vityaz.bot.model.User;
import ru.vityaz.bot.repository.UserRepository;

import java.util.Date;

@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final AuditService auditService;

    public UserService(UserRepository userRepository, AuditService auditService) {
        this.userRepository = userRepository;
        this.auditService = auditService;
    }

    public void save(Message message) {
        var chatId = message.getChatId();
        var chat = message.getChat();
        User userToSave = new User();
        if (isUserInDatabase(chatId)) {
            userToSave = userRepository.findById(message.getChatId()).orElseThrow();
            auditService.logChanges("User with id: " + chatId + "is changed");
        } else {
            auditService.logChanges("A new user with id: " + chatId + "is saved");
        }
        userToSave.setChatId(chatId);
        userToSave.setUsername(chat.getUserName());
        userToSave.setFirstName(chat.getFirstName());
        userToSave.setLastName(chat.getLastName());
        userToSave.setStartDate(new Date());

        userRepository.save(userToSave);
    }

    public void delete(Long chatId) {
        userRepository.delete(getUserStoredData(chatId));
    }

    public User getUserStoredData(Long chatId) {
        return isUserInDatabase(chatId) ? userRepository.findById(chatId).orElseThrow() : null;
    }

    public Boolean isUserInDatabase(Long chatId) {
        return userRepository.findById(chatId).isPresent();
    }
}
