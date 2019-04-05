package ru.relastic.meet017dagger2.dagger;

import android.content.Context;
import android.support.annotation.NonNull;

import dagger.Module;
import dagger.Provides;
import ru.relastic.meet017dagger2.presenter.MyPresenter;

@Module
public class UIModule {

    @Provides
    @Userspace
    public MyPresenter provideMyPresenter(Context context) {
        System.out.println("-------------- СОЗДАНИЕ ЭКЗЕМПЛЯРА MyPresenter");
        return new MyPresenter(context);
    }
}
