package app.DTO;

import lombok.Data;

@Data
public class ActivityDTO {
    private Integer id;
    private String activityName;
    private CityInfoDTO cityInfo;
    private WeatherInfoDTO weatherInfo;
    private String comment;
}
