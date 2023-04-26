package ru.vityaz.bot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.vityaz.bot.model.entity.weather.WeatherEntity;

import java.time.LocalDateTime;
import java.util.Date;

@Repository
public interface WeatherRepository extends JpaRepository<WeatherEntity, Long> {

    @Query("select w.localtime from WeatherEntity w where w.city=:city")
    public LocalDateTime getLastWeatherDateCheckForCity(String city);

    @Query("select w from WeatherEntity w")
    public WeatherEntity getLastWeatherForCity(String city);
}
