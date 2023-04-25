package ru.vityaz.bot.model.entity.weather.current;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.vityaz.bot.model.entity.weather.Weather;

@Entity
@Table(name = "WEATHER_CURRENT")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Current {
    private Double temp_c;

    private Double wind_mph;
    private Double wind_kph;
    private Double feelslike_c;
    @OneToOne(mappedBy = "current")
    private Condition condition;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "WEATHER")
    @Id
    private Weather weather;

    @Override
    public String toString() {
        return "Current{" +
                "temp_c=" + temp_c +
                ", condition=" + condition +
                '}';
    }
}
