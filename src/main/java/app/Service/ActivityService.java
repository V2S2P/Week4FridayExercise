package app.Service;

import app.DAO.ActivityDAO;
import app.DTO.ActivityDTO;
import app.DTO.CityInfoDTO;
import app.DTO.WeatherInfoDTO;

public class ActivityService {
    private final CityFetch cityFetch = new  CityFetch();
    private final WeatherFetch weatherFetch = new  WeatherFetch();
    private final ActivityDAO activityDAO;

    public ActivityService(ActivityDAO activityDAO) {
        this.activityDAO = activityDAO;
    }

    public ActivityDTO createActivity(String activityType, String cityName) {
        CityInfoDTO city = cityFetch.getCityInfo(cityName);
        if (city == null) {
            return null;
        }
        WeatherInfoDTO weather = weatherFetch.getWeatherInfo(city.getLatitude(), city.getLongitude());

        ActivityDTO activityDTO = new ActivityDTO();
        activityDTO.setActivityName(activityType);
        activityDTO.setCityInfo(city);
        activityDTO.setWeatherInfo(weather);

        if (weather != null) {
            activityDTO.setComment("Weather fetched successfully for " + city.getName());
        }else {
            activityDTO.setComment("Weather could not be fetched for " + city.getName());
        }
        return activityDTO;
    }
}
