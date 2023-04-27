package ru.vityaz.bot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.vityaz.bot.model.entity.weather.WeatherEntity;

import java.time.LocalDateTime;
import java.util.Date;

@Repository
public interface WeatherRepository extends JpaRepository<WeatherEntity, Long> {

    @Query(value = "select timestamp from WEATHER where city=':city' limit 1", nativeQuery = true)
    public LocalDateTime getLastWeatherDateCheckForCity(String city);

    @Query(value = "select * from WEATHER where city=':city' limit 1", nativeQuery = true)
    public WeatherEntity getLastWeatherForCity(String city);
}
