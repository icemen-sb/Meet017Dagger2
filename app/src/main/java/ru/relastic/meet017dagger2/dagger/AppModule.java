package ru.relastic.meet017dagger2.dagger;

import android.content.Context;
import android.support.annotation.NonNull;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.relastic.meet017dagger2.reposirory.DataLoader;
import ru.relastic.meet017dagger2.reposirory.ImageLoader;
import ru.relastic.meet017dagger2.reposirory.MyAsyncImageLoad;
import ru.relastic.meet017dagger2.reposirory.RetrofitApiMapper;

@Module
public class AppModule {
    private Context mContext;
    public AppModule(@NonNull Context context) {
        System.out.println("-------------- СОЗДАНИЕ ЭКЗЕМПЛЯРА Context");
        mContext = context;
    }

    @Provides
    @Singleton
    public Context provideContext(){
        System.out.println("--------------   ЗАПРОС ЭКЗЕМПЛЯРА Context");
        return mContext;
    }

    @Provides
    @Singleton
    public DataLoader provideDataLoader() {
        System.out.println("-------------- СОЗДАНИЕ ЭКЗЕМПЛЯРА DataLoader");
        return new RetrofitApiMapper();
    }

    @Provides
    @Singleton
    public ImageLoader provideImageLoader(@NonNull Context context) {
        System.out.println("-------------- СОЗДАНИЕ ЭКЗЕМПЛЯРА ImageLoader");
        return new MyAsyncImageLoad(context);
    }
}
