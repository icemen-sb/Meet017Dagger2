package ru.relastic.meet017dagger2.dagger;

import dagger.Subcomponent;
import ru.relastic.meet017dagger2.DetailsActivity;
import ru.relastic.meet017dagger2.MainActivity;
import ru.relastic.meet017dagger2.domain.MyService;

@Subcomponent (modules = {UIModule.class})
@Userspace
public interface UIComponent {
    void inject(MyService myService);
    void inject(MainActivity mainActivity);
    void inject(DetailsActivity detailsActivity);
}
