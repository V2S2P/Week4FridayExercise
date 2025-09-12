package app.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherInfoDTO {
    private Integer id;
    @JsonProperty("temperature_2m")
    private String temperature;
    @JsonProperty("relative_humidity_2m")
    private String relativeHumidity;
    @JsonProperty("precipitation_probability")
    private String precipitation;
    @JsonProperty("wind_speed_10m")
    private String windSpeed;
    @JsonProperty("timezone")
    private String timeZone;
}
