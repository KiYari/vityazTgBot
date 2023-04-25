package ru.vityaz.bot.service.external.weather;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.vityaz.bot.model.entity.weather.Weather;
import ru.vityaz.bot.repository.WeatherRepository;
import ru.vityaz.bot.service.AuditService;
import ru.vityaz.bot.service.external.weather.WeatherApiService;

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
        save(weather);
        return weather.toString();
    }

    public String getWeather(String city, Date date) {
        System.out.println(weatherRepository.getLastWeatherForCity(city));
        System.out.println(weatherRepository.getLastWeatherForCity(city) != null);
        if (weatherRepository.getLastWeatherForCity(city) == null ||
                date.getTime() - weatherRepository.getLastWeatherDateCheckForCity(city).getTime() < 2 * HOUR) {
            System.out.println("IN API CALL");
            return getWeatherFromApi(city);
        } else {
            System.out.println("IN BD CALL");
            return weatherRepository.getLastWeatherForCity(city).toString();
        }
    }

    private void save(Weather weather) {
        weatherRepository.saveAndFlush(weather);
        auditService.logChanges("weather: " + weather + "was added to db");
    }

}
