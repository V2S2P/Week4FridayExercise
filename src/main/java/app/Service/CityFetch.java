package app.Service;

import app.DTO.CityInfoDTO;
import app.DTO.ResponseDTO;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class CityFetch {
    private final FetchTools fetchTools = new  FetchTools();
    //Uses the fetchTools method to get data from the City API
    public CityInfoDTO getCityInfo(String cityName){
        String encodedCity = URLEncoder.encode(cityName, StandardCharsets.UTF_8);
        String url = "https://geocoding-api.open-meteo.com/v1/search?name="+encodedCity+"&count=10&language=en&format=json";
        //Returns a CityInfoDTO through a ResponseDTO
        ResponseDTO response = fetchTools.getFromApi(url, ResponseDTO.class);
        return response != null && response.getCityInfoDTOList() != null && !response.getCityInfoDTOList().isEmpty()
                ? response.getCityInfoDTOList().getFirst()
                : null;
    }
}
