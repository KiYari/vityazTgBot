package ru.vityaz.bot.model;

import jakarta.persistence.Entity;
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

    @Override
    public String toString() {
        return "User id: " + chatId +
                "\n First name: " + firstName +
                "\n Last name: " + lastName +
                "\n Username: " + username +
                "\n Register date: " + startDate;
    }
}
