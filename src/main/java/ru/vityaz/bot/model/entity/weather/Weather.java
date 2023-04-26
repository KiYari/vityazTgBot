package ru.vityaz.bot.model.entity.weather;

import jakarta.persistence.*;
import lombok.*;
import ru.vityaz.bot.model.entity.weather.current.Condition;
import ru.vityaz.bot.model.entity.weather.current.Current;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
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

    public static Weather parseEntity(WeatherEntity weather) {
        return Weather.builder()
                .location(new Location(weather.getCity(), weather.getRegion(), weather.getCountry(), weather.getLocaltime(), weather.getTimestamp(), null))
                .current(new Current(weather.getTemp_c(), weather.getWind_mph(), weather.getWind_kph(), weather.getFeelslike_c(),
                        new Condition(weather.getCondition(), weather.getConditionIcon(), weather.getConditionCode(), null),
                        null))
                .build();
    }
}
