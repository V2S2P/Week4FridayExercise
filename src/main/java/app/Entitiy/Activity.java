package app.Entitiy;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private CityInfo cityInfo;

    @OneToOne
    private WeatherInfo weatherInfo;

    private String comment;
    private String activityName;
}
