package ru.relastic.meet017dagger2.reposirory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.relastic.meet017dagger2.domain.MyService;
import ru.relastic.meet017dagger2.domain.WeatherEntity;
import ru.relastic.meet017dagger2.domain.WeatherEntityCurrent;


public class RetrofitApiMapper implements DataLoader{
    private static final String VERSION_API = "2.5/";
    private static final String BASE_URL = "http://api.openweathermap.org/data/";
    public static final String PATH_CURRENT = "weather";
    public static final String PATH_FORECAST = "forecast";
    public static final String APPID = "72bda240bd2e87975920efda89b36efa";

    private final RetrofitHelper helper;

    public RetrofitApiMapper() {
        this.helper = new RetrofitHelper();
    }

    @Override
    public void requestWeatherByCitiId(
            long citi_id,
            final MyService.IDataLoaderCallback<WeatherEntity> callback) {

        Callback<WeatherEntity> callbacks = new Callback<WeatherEntity>() {
            @Override
            public void onResponse(Call<WeatherEntity> call, Response<WeatherEntity> response) {
                callback.onResponseData(response.body());
            }
            @Override
            public void onFailure(Call<WeatherEntity> call, Throwable t) {}
        };
        helper.getService().getWeatherForecastByCitiId(citi_id, APPID).enqueue(callbacks);
    }

    @Override
    public void requestWeatherByCitiIdCurrent(
            long citi_id,
            final MyService.IDataLoaderCallback<WeatherEntityCurrent> callback) {

        Callback<WeatherEntityCurrent> callbacks = new Callback<WeatherEntityCurrent>() {
            @Override
            public void onResponse(Call<WeatherEntityCurrent> call, Response<WeatherEntityCurrent> response) {
                callback.onResponseData(response.body());
            }
            @Override
            public void onFailure(Call<WeatherEntityCurrent> call, Throwable t) {}
        };
        helper.getService().getWeatherCurrentByCitiId(citi_id, APPID).enqueue(callbacks);
    }

    public static WeatherEntity convertJSON(String json_string) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.fromJson(json_string, WeatherEntity.class);
    }

    public static String convertToString(WeatherEntity wheather) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.toJson(wheather).toString();
    }

    public class RetrofitHelper {

        public RetrofitHelper () { }
        public IRetrofitCallback getService() {
            Gson gson = new GsonBuilder().setLenient().create();

            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient.Builder client = new OkHttpClient.Builder()
                    .addInterceptor(interceptor);

            //Retrofit retrofit = new Retrofit.Builder()
            //        .baseUrl(BASE_URL+VERSION_API)
            //        .addConverterFactory(GsonConverterFactory.create(gson))
            //        .client(client.build())
            //        .build();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL+VERSION_API)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
            return retrofit.create(IRetrofitCallback.class);
        }
    }
}
