package ru.relastic.meet015architecture.reposiroty;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;
import ru.relastic.meet015architecture.domain.WeatherEntity;
import ru.relastic.meet015architecture.domain.WeatherEntityCurrent;

public interface WeatherService {

    @Headers({"Content-Type: application/json"})
    @GET(RetrofitHelper.PATH_FORECAST)
    Call<WeatherEntity> getWeatherForecastByCitiId (@Query("id") long id,
                                                    @Query("appid") String appid);

    @Headers({"Content-Type: application/json"})
    @GET(RetrofitHelper.PATH_CURRENT)
    Call<WeatherEntityCurrent> getWeatherCurrentByCitiId (@Query("id") long id,
                                                          @Query("appid") String appid);

}
