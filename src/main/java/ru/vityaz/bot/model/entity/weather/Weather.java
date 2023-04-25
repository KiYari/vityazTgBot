package ru.vityaz.bot.model.entity.weather;

import jakarta.persistence.*;
import lombok.*;
import ru.vityaz.bot.model.entity.weather.current.Current;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Weather {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(mappedBy = "weather")
    private Location location;
    @OneToOne(mappedBy = "weather")
    private Current current;

    @Override
    public String toString() {
        return "Weather in city " + location.getName() + ", " + location.getRegion() + ", " + location.getCountry() +
                "\nTemperature(celsius): " + current.getTemp_c() +
                "\nFeels like(celsius): " + current.getFeelslike_c() +
                "\nWeather condition: " + current.getCondition().getText() + "(" + current.getCondition().getCode() + ")";
    }
}
