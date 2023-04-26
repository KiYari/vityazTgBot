package ru.vityaz.bot.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "USERS")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class User {
    @Id
    private Long chatId;
    private String firstName;
    private String lastName;
    private String username;
    private Date startDate;
    private Boolean isAdmin;
    private Boolean isSubscribedToSend;
    private Boolean isSubscribedToWeatherAutosend;
    private String city;

    @Override
    public String toString() {
        return "User id: " + chatId +
                "\nFirst name: " + firstName +
                "\nLast name: " + lastName +
                "\nUsername: " + username +
                "\nRegister date: " + startDate +
                "\nCurrent city: " + city +
                "\nSubscribed to weather: " + isSubscribedToWeatherAutosend;
    }
}
