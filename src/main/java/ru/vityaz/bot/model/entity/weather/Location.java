package ru.vityaz.bot.model.entity.weather;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Location {
    private String name;
    private String region;
    private String country;
    @Column(name = "localtime_checked")
    private String localtime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern = "yyyy-mm-dd hh:mm")
    private LocalDateTime timestamp;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "WEATHER_ID")
    private Weather weather;

    @Override
    public String toString() {
        return "Location{" +
                "name='" + name + '\'' +
                ", region='" + region + '\'' +
                ", country='" + country + '\''+
                ", localtime='" + localtime + '\'' +
                '}';
    }
}
