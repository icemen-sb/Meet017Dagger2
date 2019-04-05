package ru.relastic.meet017dagger2.dagger;

import android.app.Application;

public class Appl extends Application {
    private static AppComponent component = null;
    private static UIComponent subcomponent = null;
    public static AppComponent getComponent() {
        return component;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        component = buildComponent();
    }

    protected AppComponent buildComponent() {

        return DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }

    public static UIComponent getSubcomponent() {
        if (subcomponent==null) {
            subcomponent = component.uiComponent(new UIModule());
            }
        return subcomponent;
    }
    public static void destroySubcomponent() {
        subcomponent = null;
    }
}
