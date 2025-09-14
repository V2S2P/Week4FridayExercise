package app;

import app.DAO.ActivityDAO;
import app.DTO.ActivityDTO;
import app.Service.ActivityService;
import app.config.HibernateConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Main {
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        try (EntityManager em = emf.createEntityManager()) {

            ActivityDAO activityDAO = new ActivityDAO(emf);
            ActivityService service = new ActivityService(activityDAO, executorService);

            long start = System.currentTimeMillis();

            ActivityDTO activityDTO = service.createActivity("Hiking", "New York");
            ActivityDTO activityDTO2 = service.createActivity("Hiking", "New York");

            long end = System.currentTimeMillis();
            System.out.println("Parallel fetch time (ms): " + (end - start));

            ActivityDTO savedActivity = activityDAO.createActivity(activityDTO);

            System.out.println("Activity saved with ID: " + savedActivity.getId());
            System.out.println("City ID: " + savedActivity.getCityInfo().getId());
            System.out.println("Weather ID: " + savedActivity.getWeatherInfo().getId());
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            executorService.shutdown();
        }
    }
}