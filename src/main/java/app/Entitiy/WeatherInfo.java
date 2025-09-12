package app.Entitiy;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class WeatherInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String temperature;
    private String relativeHumidity;
    private String precipitation;
    private String windSpeed;
    private String timeZone;
}
