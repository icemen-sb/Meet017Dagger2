package ru.relastic.meet017dagger2.reposirory;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Callback;
import ru.relastic.meet017dagger2.domain.MyService;
import ru.relastic.meet017dagger2.domain.WeatherEntity;
import ru.relastic.meet017dagger2.domain.WeatherEntityCurrent;

public interface DataLoader {

    public void requestWeatherByCitiId(
            long citi_id, final MyService.IDataLoaderCallback<WeatherEntity> callback);

    public void requestWeatherByCitiIdCurrent(
            long citi_id, final MyService.IDataLoaderCallback<WeatherEntityCurrent> callback);

}
