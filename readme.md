## 📝 Step-by-step Guide For Fetching of API Data, And Database Inserting
## 1. Define your Entities (Database models)

Entities are JPA classes that represent tables in the database.
Example: Activity entity
```java

@Data
@Entity
public class Activity {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Integer id;

    private String activityName;
    private String comment;

    @ManyToOne
    private CityInfo cityInfo;

    @OneToOne
    private WeatherInfo weatherInfo;
}
```
👉 This class maps directly to a database table and defines what will actually be stored.

## 2. Define your DTOs (API data models)

DTOs match the JSON structure returned by the external APIs. They act as temporary carriers for data fetched from APIs.

Example: CityInfoDTO and WeatherInfoDTO
```java
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CityInfoDTO {
private Integer id;
@JsonProperty("name")
private String name;
@JsonProperty("latitude")
private double latitude;
@JsonProperty("longitude")
private double longitude;
@JsonProperty("elevation")
private int elevation;
@JsonProperty("population")
private int population;
}

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
```
👉 These DTOs are shaped like the JSON you get from Open-Meteo’s APIs, so Jackson can map directly into them.

## 3. Create Fetchers (API Clients)

Fetchers are responsible for calling external APIs and deserializing JSON into DTOs.

Example: WeatherFetch
```java
public class WeatherFetch {
private final FetchTools fetchTools = new FetchTools();

    public WeatherInfoDTO getWeatherInfo(double latitude, double longitude) {
        String url = "https://api.open-meteo.com/v1/forecast?latitude=" + latitude +
                     "&longitude=" + longitude +
                     "&hourly=temperature_2m,relative_humidity_2m,precipitation_probability,wind_speed_10m";

        ResponseDTO response = fetchTools.getFromApi(url, ResponseDTO.class);
        return response != null ? response.getWeatherInfoDTO() : null;
    }
}
```
👉 Fetchers only return DTOs, not entities. They keep the API logic separate from persistence.

## 4. Build the DAO (Database access)

DAO methods take DTOs, convert them to entities, and persist them in the database.

Example: ActivityDAO
```java
public ActivityDTO createActivity(ActivityDTO dto) {
try (EntityManager em = emf.createEntityManager()) {
em.getTransaction().begin();

        Activity entity = new Activity();
        entity.setActivityName(dto.getActivityName());
        entity.setComment(dto.getComment());

        CityInfo cityEntity = new CityInfo();
        cityEntity.setName(dto.getCityInfo().getName());
        em.persist(cityEntity);

        WeatherInfo weatherEntity = new WeatherInfo();
        weatherEntity.setTemperature(dto.getWeatherInfo().getTemperature());
        weatherEntity.setWindSpeed(dto.getWeatherInfo().getWindSpeed());
        weatherEntity.setTimeZone(dto.getWeatherInfo().getTimeZone());
        em.persist(weatherEntity);

        entity.setCityInfo(cityEntity);
        entity.setWeatherInfo(weatherEntity);

        em.persist(entity);
        em.getTransaction().commit();

        dto.setId(entity.getId());
        return dto;
    }
}
```
👉 DAO = Translator from DTO → Entity → Database.

## 5. Create the Service layer

The service orchestrates the full workflow:

Fetch city + weather data from APIs.

Combine them into an ActivityDTO.

Pass it to DAO for persistence.

Example: ActivityService
```java
public class ActivityService {
private final CityFetch cityFetch = new CityFetch();
private final WeatherFetch weatherFetch = new WeatherFetch();
private final ActivityDAO activityDAO;

    public ActivityService(ActivityDAO activityDAO) {
        this.activityDAO = activityDAO;
    }

    public ActivityDTO createActivity(String activityType, String cityName) {
        CityInfoDTO city = cityFetch.getCityInfo(cityName);
        if (city == null) return null;

        WeatherInfoDTO weather = weatherFetch.getWeatherInfo(city.getLatitude(), city.getLongitude());

        ActivityDTO activityDTO = new ActivityDTO();
        activityDTO.setActivityName(activityType);
        activityDTO.setCityInfo(city);
        activityDTO.setWeatherInfo(weather);

        if (weather != null) {
            activityDTO.setComment("Weather fetched successfully for " + city.getName());
        } else {
            activityDTO.setComment("Weather could not be fetched for " + city.getName());
        }

        return activityDAO.createActivity(activityDTO);
    }
}
```
👉 Service = Conductor. It knows how to “orchestrate” API fetch + mapping + database persistence.

## 6. Wire it all together (Main / Controller)

Finally, we glue everything together in Main.

Example:
```java
ActivityDAO activityDAO = new ActivityDAO(emf);
ActivityService service = new ActivityService(activityDAO);

ActivityDTO activity1 = service.createActivity("Hiking", "Kongens Lyngby");
ActivityDTO activity2 = service.createActivity("Running", "Gladsaxe");
ActivityDTO activity3 = service.createActivity("Biking", "Berlin");

ActivityDTO savedActivity = activityDAO.createActivity(activityDTO);
ActivityDTO savedActivity2 = activityDAO.createActivity(activityDTO2);
ActivityDTO savedActivity3 = activityDAO.createActivity(activityDTO3);
```
## 🚀 Final Flow (Big Picture)
```plainText
1. flowchart LR
2. API[🌐 Open-Meteo API] -->|JSON| DTO[📦 DTOs]
3. DTO --> Service[⚙️ Service Layer]
4. Service --> DAO[🗄️ DAO]
5. DAO --> Entity[🏛️ Entities]
6. Entity --> DB[(💾 Database)]
```
```plainText
1. Entities → Define database tables.

2. DTOs → Match API JSON.

3. Fetchers → Call APIs and return DTOs.

4. DAO → Convert DTOs into Entities, save to DB.

5. Service → Orchestrate fetch + map + save.

6. Main → Run the workflow.
```
## (OPTIONAL-ExecutorService(threadPools))

To make things faster, using a threadpool and ExecutorService is a good way to make things faster.

Normally calling fetchers like cityFetch.getCityInfo() and weatherFetch.getWeatherInfo() would have them run sequentially.
So that means the program waits for the first HTTP request to finish.

Using ExecutorService means we can create a thread pool so we can run tasks asynchronously in seperate threads.

Example:
```java
Future<CityInfoDTO> cityFuture = executor.submit(() -> cityFetch.getCityInfo(cityName));
Future<WeatherInfoDTO> weatherFuture = executor.submit(() -> {
    CityInfoDTO city = cityFuture.get();
    if (city != null) {
        return weatherFetch.getWeatherInfo(city.getLatitude(), city.getLongitude());
    }
    return null;
});
```
cityFuture starts fetching city info in one thread.

weatherFuture is also submitted right away, but it waits (cityFuture.get()) before it can run.

This pattern allows concurrency but ensures weather fetch only starts after city fetch succeeds.

## What Future means
🔹 What Future means

A Future<T> is basically a promise of a result that will be available later.

When you call executor.submit(task), it returns a Future.

That Future doesn’t hold the result immediately (since the task might still be running).

When you call future.get():

If the task is done → you get the result immediately.

If not → the call blocks until the result is ready.

So in your code:

cityFuture.get() → blocks until the city info is fetched.

weatherFuture.get() → blocks until weather info is ready.

## ✅ Summary For ExecutorService

1. ExecutorService = manages threads so tasks can run in parallel.

2. Future<T> = a handle to get the result later (blocks if not ready yet).

3. In ActivityService, this improves performance by fetching city and weather in parallel.

4. For small apps → executor inside the service is fine.

5. For bigger apps → create the executor in main (or DI framework) and inject it into services.