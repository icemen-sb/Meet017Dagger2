package ru.relastic.meet017dagger2.reposirory;

import android.content.Context;
import android.support.annotation.NonNull;
import ru.relastic.meet017dagger2.domain.MyService;

public interface ImageLoader {
    public void loadImage(@NonNull MyService.IImageLoaderCallback callback, String icon_id);
}
