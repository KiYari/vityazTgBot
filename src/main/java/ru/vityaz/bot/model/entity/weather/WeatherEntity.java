package ru.vityaz.bot.model.entity.weather;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "WEATHER")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class WeatherEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double temp_c;
    private Double wind_mph;
    private Double wind_kph;
    private Double feelslike_c;
    private String condition;
    private String conditionIcon;
    private Integer conditionCode;
    private String city;
    private String region;
    private String country;

    @Column(name = "localtime_checked")
    private String localtime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern = "yyyy-mm-dd hh:mm")
    private LocalDateTime timestamp;

    @Override
    public String toString() {
        return "WeatherEntity{" +
                "id=" + id +
                ", temp_c=" + temp_c +
                ", wind_mph=" + wind_mph +
                ", wind_kph=" + wind_kph +
                ", feelslike_c=" + feelslike_c +
                ", condition='" + condition + '\'' +
                ", conditionIcon='" + conditionIcon + '\'' +
                ", conditionCode=" + conditionCode +
                ", city='" + city + '\'' +
                ", region='" + region + '\'' +
                ", country='" + country + '\'' +
                ", localtime='" + localtime + '\'' +
                '}';
    }

    public static WeatherEntity toEntity(Weather weather) {
        return WeatherEntity.builder()
                .temp_c(weather.getCurrent().getTemp_c())
                .feelslike_c(weather.getCurrent().getFeelslike_c())
                .wind_kph(weather.getCurrent().getWind_kph())
                .wind_mph(weather.getCurrent().getWind_mph())
                .condition(weather.getCurrent().getCondition().getText())
                .conditionIcon(weather.getCurrent().getCondition().getIcon())
                .conditionCode(weather.getCurrent().getCondition().getCode())
                .city(weather.getLocation().getName())
                .region(weather.getLocation().getName())
                .country(weather.getLocation().getCountry())
                .localtime(weather.getLocation().getLocaltime())
                .timestamp(LocalDateTime.now())
                .build();
    }
}
