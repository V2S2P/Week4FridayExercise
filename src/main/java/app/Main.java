package app;

import app.DAO.ActivityDAO;
import app.DTO.ActivityDTO;
import app.Service.ActivityService;
import app.config.HibernateConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;


public class Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        try (EntityManager em = emf.createEntityManager()) {

            ActivityDAO activityDAO = new ActivityDAO(emf);
            ActivityService service = new ActivityService(activityDAO);

            ActivityDTO activityDTO = service.createActivity("Hiking", "Kongens Lyngby");
            ActivityDTO activityDTO2 = service.createActivity("Running", "Gladsaxe");
            ActivityDTO activityDTO3 = service.createActivity("Biking", "Berlin");

            ActivityDTO savedActivity = activityDAO.createActivity(activityDTO);
            ActivityDTO savedActivity2 = activityDAO.createActivity(activityDTO2);
            ActivityDTO savedActivity3 = activityDAO.createActivity(activityDTO3);

            System.out.println("Activity saved with ID: " + savedActivity.getId());
            System.out.println("City ID: " + savedActivity.getCityInfo().getId());
            System.out.println("Weather ID: " + savedActivity.getWeatherInfo().getId());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}