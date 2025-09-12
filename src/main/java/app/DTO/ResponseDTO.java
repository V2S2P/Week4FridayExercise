package app.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseDTO {
    @JsonProperty("results")
    private List<CityInfoDTO> cityInfoDTOList;

    @JsonProperty("hourly_units")
    private WeatherInfoDTO weatherInfoDTOList;
}
