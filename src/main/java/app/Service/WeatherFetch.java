package app.Service;

import app.DTO.ResponseDTO;
import app.DTO.WeatherInfoDTO;

public class WeatherFetch {
    private final FetchTools fetchTools = new  FetchTools();
    public WeatherInfoDTO getWeatherInfo(double latitude, double longitude){
        String url = "https://api.open-meteo.com/v1/forecast?latitude=" + latitude + "&longitude=" + longitude +
                "&hourly=temperature_2m,relative_humidity_2m,precipitation_probability,wind_speed_10m";

        ResponseDTO response = fetchTools.getFromApi(url, ResponseDTO.class);
        return response != null ? response.getWeatherInfoDTOList() : null;
    }
}
