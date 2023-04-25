package ru.vityaz.bot.model.entity.weather;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "WEATHER_LOCATION")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Location {
    private String name;
    private String region;
    private String country;
    @DateTimeFormat(pattern = "yyyy-mm-dd hh:mm")
    @Column(name = "timestamp")
    private String localtime;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "WEATHER")
    @Id
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
