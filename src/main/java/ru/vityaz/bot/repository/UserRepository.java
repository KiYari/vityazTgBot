package ru.vityaz.bot.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.vityaz.bot.model.entity.User;

import java.util.Set;

@Repository
@Transactional
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u.chatId FROM User u")
    public Set<Long> getAllChatIdsOfUsers();
    @Query("SELECT u.isAdmin FROM User u where u.chatId = :chatId")
    public Boolean isAdmin(Long chatId);

    @Query("SELECT u.isSubscribedToSend FROM User u where u.chatId = :chatId")
    public Boolean isSubscribedToSend(Long chatId);
    @Modifying
    @Query("UPDATE User u SET u.isSubscribedToSend=(not u.isSubscribedToSend) WHERE u.chatId=:chatId")
    public void switchIsSubscribedToSend(Long chatId);

    @Query("SELECT u.isSubscribedToWeatherAutosend FROM User u where u.chatId = :chatId")
    public Boolean isSubscribedToWeather(Long chatId);
    @Modifying
    @Query("UPDATE User u SET u.isSubscribedToWeatherAutosend=(not u.isSubscribedToWeatherAutosend) WHERE u.chatId=:chatId")
    public void switchIsSubscribedToWeatherAutosend(Long chatId);

    @Query("SELECT u.city FROM User u WHERE u.chatId=:chatId")
    public String findUserCityByChatId(Long chatId);
}
