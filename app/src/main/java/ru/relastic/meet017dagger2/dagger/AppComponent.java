package ru.relastic.meet017dagger2.dagger;


import javax.inject.Singleton;

import dagger.Component;

@Component (modules = {AppModule.class})
@Singleton
public interface AppComponent {
    UIComponent uiComponent(UIModule uiModule);
}
