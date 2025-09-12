package app.Entitiy;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Entity
public class CityInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private double latitude;
    private double longitude;
    private int elevation;
    private int population;

    @OneToMany(mappedBy = "cityInfo", cascade = CascadeType.ALL,  fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Activity> activities;
}
