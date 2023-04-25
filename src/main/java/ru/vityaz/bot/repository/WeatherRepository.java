package ru.vityaz.bot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.vityaz.bot.model.entity.weather.Weather;

import java.util.Date;

@Repository
public interface WeatherRepository extends JpaRepository<Weather, Long> {

    @Query("select l.localtime from Location l ")
    public Date getLastWeatherDateCheckForCity(String city);

    @Query("select w from Weather w")
    public Weather getLastWeatherForCity(String city);
}
