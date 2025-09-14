package app.Service;

import app.DAO.ActivityDAO;
import app.DTO.ActivityDTO;
import app.DTO.CityInfoDTO;
import app.DTO.WeatherInfoDTO;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ActivityService {
    private final CityFetch cityFetch = new CityFetch();
    private final WeatherFetch weatherFetch = new WeatherFetch();
    private final ActivityDAO activityDAO;
    private final ExecutorService executorService;

    public ActivityService(ActivityDAO activityDAO, ExecutorService executorService) {
        this.activityDAO = activityDAO;
        this.executorService = executorService;
    }

    public ActivityDTO createActivity(String activityType, String cityName) {
        try {
            Future<CityInfoDTO> cityFuture = executorService.submit(() -> cityFetch.getCityInfo(cityName));
            Future<WeatherInfoDTO> weatherFuture = executorService.submit(() -> {
                CityInfoDTO city = cityFuture.get();
                if (city != null) {
                    return weatherFetch.getWeatherInfo(city.getLatitude(), city.getLongitude());
                }
                return null;
            });

            CityInfoDTO city = cityFuture.get();
            WeatherInfoDTO weather = weatherFuture.get();

            ActivityDTO activityDTO = new ActivityDTO();
            activityDTO.setActivityName(activityType);
            activityDTO.setCityInfo(city);
            activityDTO.setWeatherInfo(weather);

            if (weather != null) {
                activityDTO.setComment("Weather fetched successfully for " + city.getName());
            } else {
                activityDTO.setComment("Weather could not be fetched for " + city.getName());
            }
            return activityDTO;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
