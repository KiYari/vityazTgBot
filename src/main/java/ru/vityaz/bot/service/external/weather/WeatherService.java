package ru.vityaz.bot.service.external.weather;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.vityaz.bot.model.entity.weather.Weather;
import ru.vityaz.bot.model.entity.weather.WeatherEntity;
import ru.vityaz.bot.repository.WeatherRepository;
import ru.vityaz.bot.service.AuditService;
import ru.vityaz.bot.service.external.weather.WeatherApiService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class WeatherService {
    private final WeatherRepository weatherRepository;
    private final WeatherApiService weatherApiService;
    private final AuditService auditService;
    private final Long HOUR = (long) (1 * 60 * 60 * 1000);

    @Value("${weather.token}")
    private String weatherToken;

    private String getWeatherFromApi(String city) {
        Weather weather = weatherApiService.getWeather(city, weatherToken);
        save(WeatherEntity.toEntity(weather));
        return weather.toString();
    }

    public String getWeather(String city, LocalDateTime date, Long chatId) {
        LocalDateTime lastWeatehrCheckForCity = weatherRepository.getLastWeatherDateCheckForCity(city);
        if (lastWeatehrCheckForCity == null ||
                ChronoUnit.HOURS.between(date, lastWeatehrCheckForCity) < 2) {
            auditService.logChanges("Sent weather to user " + chatId);
            return getWeatherFromApi(city);
        } else {
            return Weather.parseEntity(weatherRepository.getLastWeatherForCity(city)).toString();
        }
    }

    private void save(WeatherEntity weather) {
        weatherRepository.save(weather);
        auditService.logChanges("weather: " + weather.toString() + "was added to db");
    }

}
