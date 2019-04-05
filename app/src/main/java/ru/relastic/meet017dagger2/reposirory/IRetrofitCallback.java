package ru.relastic.meet017dagger2.reposirory;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;
import ru.relastic.meet017dagger2.domain.WeatherEntity;
import ru.relastic.meet017dagger2.domain.WeatherEntityCurrent;

public interface IRetrofitCallback {

    @Headers({"Content-Type: application/json"})
    @GET(RetrofitApiMapper.PATH_FORECAST)
    Call<WeatherEntity> getWeatherForecastByCitiId (@Query("id") long id,
                                                    @Query("appid") String appid);

    @Headers({"Content-Type: application/json"})
    @GET(RetrofitApiMapper.PATH_CURRENT)
    Call<WeatherEntityCurrent> getWeatherCurrentByCitiId (@Query("id") long id,
                                                          @Query("appid") String appid);

}
