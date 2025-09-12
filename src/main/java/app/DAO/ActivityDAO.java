package app.DAO;

import app.DTO.ActivityDTO;
import app.DTO.CityInfoDTO;
import app.Entitiy.Activity;
import app.Entitiy.CityInfo;
import app.Entitiy.WeatherInfo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

public class ActivityDAO {
    private final EntityManagerFactory emf;
    public  ActivityDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }
    public ActivityDTO createActivity(ActivityDTO dto) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            // Convert DTO -> entity
            Activity entity = new Activity();
            entity.setComment(dto.getComment());
            entity.setActivityName(dto.getActivityName()); // make sure Activity has this field

            // Convert CityInfoDTO -> CityInfo entity
            CityInfo cityEntity = new CityInfo();
            cityEntity.setName(dto.getCityInfo().getName()); // entity setter
            cityEntity.setElevation(dto.getCityInfo().getElevation());
            cityEntity.setLatitude(dto.getCityInfo().getLatitude());
            cityEntity.setLongitude(dto.getCityInfo().getLongitude());
            cityEntity.setPopulation(dto.getCityInfo().getPopulation());
            em.persist(cityEntity);

            // Convert WeatherInfoDTO -> WeatherInfo entity
            WeatherInfo weatherEntity = new WeatherInfo();
            weatherEntity.setTemperature(dto.getWeatherInfo().getTemperature());
            weatherEntity.setWindSpeed(dto.getWeatherInfo().getWindSpeed());
            weatherEntity.setRelativeHumidity(dto.getWeatherInfo().getRelativeHumidity());
            weatherEntity.setPrecipitation(dto.getWeatherInfo().getPrecipitation());
            weatherEntity.setTimeZone(dto.getWeatherInfo().getTimeZone());
            em.persist(weatherEntity);

            // Link entities (⚠️ this is on the entity, not DTO!)
            entity.setCityInfo(cityEntity);      // Activity entity expects CityInfo
            entity.setWeatherInfo(weatherEntity); // Activity entity expects WeatherInfo

            // Persist activity
            em.persist(entity);
            em.getTransaction().commit();

            // Update DTO with generated ids
            dto.setId(entity.getId());
            dto.getCityInfo().setId(cityEntity.getId());
            dto.getWeatherInfo().setId(weatherEntity.getId());

            return dto;
        }
    }
    public Activity getActivityById(int id) {
        try(EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Activity activity = em.find(Activity.class, id);
            em.getTransaction().commit();
            return activity;
        }
    }
    public void updateActivity(Activity activity) {
        try(EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.merge(activity);
            em.getTransaction().commit();
            System.out.println("Activity updated");
        }
    }
    public void deleteActivityById(int id) {
        try(EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Activity activity = em.find(Activity.class, id);
            em.remove(activity);
            em.getTransaction().commit();
        }
    }
}
