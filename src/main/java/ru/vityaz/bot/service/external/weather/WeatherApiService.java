package ru.vityaz.bot.service.external.weather;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.vityaz.bot.model.entity.weather.Weather;
import ru.vityaz.bot.service.AuditService;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@PropertySource("application.properties")
public class WeatherApiService {
    private final RestTemplate restTemplate;
    private final String url = "http://api.weatherapi.com/v1/current.json?key=";
    private final AuditService auditService;

    protected Weather getWeather(String city, String weatherToken) {
        Weather weather = restTemplate.getForObject(url + weatherToken + "&q=" + city, Weather.class);
        auditService.logChanges("Weather " + weatherToken + " was written to DB");
        return weather;
    }
}
