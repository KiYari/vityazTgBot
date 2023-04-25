package ru.vityaz.bot.model.entity.weather.current;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.vityaz.bot.model.entity.weather.current.Current;

@Entity
@Table(name = "WEATHER_CURRENT_CONDITION")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Condition {
    private String text;
    private String icon;
    private Integer code;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "CURRENT_ID")
    @Id
    private Current current;

    @Override
    public String toString() {
        return "Condition{" +
                "text='" + text + '\'' +
                ", icon='" + icon + '\'' +
                ", code=" + code +
                '}';
    }
}
