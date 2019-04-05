package ru.relastic.meet017dagger2.domain;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import javax.inject.Inject;

import ru.relastic.meet017dagger2.dagger.Appl;
import ru.relastic.meet017dagger2.reposirory.DataLoader;
import ru.relastic.meet017dagger2.reposirory.ImageLoader;

//Domain
public class MyService extends Service {
    public static final String REQUEST_CITY_KEY = "request_city_key";
    public static final String REQUEST_IS_TODAY_KEY = "request_is_today_key";
    public static final String REQUEST_ICON_KEY = "request_icon_key";
    public static final String REQUEST_ICON_CALLBACK_KEY = "request_icon_callback_key";
    public static final int WHAT_CLIENT_CONNECTED = 1;
    public static final int WHAT_MESSAGE_LIST = 2;
    public static final int WHAT_MESSAGE_ITEM = 3;
    public static final int WHAT_REQUEST_ITEM = 4;
    public static final int WHAT_REQUEST_LIST = 5;
    public static final int WHAT_REQUEST_ICON = 6;
    public static final int WHAT_MESSAGE_ICON = 7;

    @Inject
    public ImageLoader mImageLoader;

    @Inject
    public DataLoader mDataLoader;

    private MyServiceHandler myServiceHandler = new MyServiceHandler();

    private final Messenger mService = new Messenger(myServiceHandler);

    @Override
    public void onCreate() {
        Appl.getSubcomponent().inject(this);
        System.out.println("------------------- SERVICE STARTED");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mService.getBinder();
    }

    public static Intent getIntent(Context context){
        return new Intent(context, MyService.class);
    }

    public interface IDataLoaderCallback<T> {
        public void onResponseData(T data);
    }
    public interface IImageLoaderCallback {
        public void onLoadedImage(Bitmap bitmap);
    }


    class MyServiceHandler extends Handler {
        //Use cases


        public MyServiceHandler() {
            super(Looper.getMainLooper());
        }

        @Override
        public void handleMessage(Message msg) {
            final Messenger service = new Messenger(msg.getTarget());
            final Messenger client = msg.replyTo;
            final Message msgResponse = new Message();
            msgResponse.copyFrom(msg);
            long id = msg.getData().getLong(REQUEST_CITY_KEY);
            switch (msg.what) {
                case WHAT_CLIENT_CONNECTED:
                    break;
                case WHAT_REQUEST_LIST:
                    mDataLoader.requestWeatherByCitiId(
                            id,
                            new MyService.IDataLoaderCallback<WeatherEntity>() {
                                @Override
                                public void onResponseData(WeatherEntity data) {
                                    msgResponse.what = WHAT_MESSAGE_LIST;
                                    msgResponse.obj = data;
                                    msgResponse.replyTo = service;
                                    try {
                                        client.send(msgResponse);
                                    } catch (RemoteException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                    break;
                case WHAT_REQUEST_ITEM:
                    mDataLoader.requestWeatherByCitiIdCurrent(
                            id,
                            new MyService.IDataLoaderCallback<WeatherEntityCurrent>() {
                                @Override
                                public void onResponseData(WeatherEntityCurrent data) {
                                    msgResponse.what = WHAT_MESSAGE_ITEM;
                                    msgResponse.obj = data;
                                    msgResponse.replyTo = service;
                                    try {
                                        client.send(msgResponse);
                                    } catch (RemoteException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                    break;
                case WHAT_REQUEST_ICON:
                    final String icon_id = msg.getData().getString(REQUEST_ICON_KEY);
                    mImageLoader.loadImage(
                            new MyService.IImageLoaderCallback() {
                                @Override
                                public void onLoadedImage(Bitmap bitmap) {
                                    msgResponse.what = WHAT_MESSAGE_ICON;
                                    msgResponse.obj = bitmap;
                                    msgResponse.replyTo = service;
                                    try {
                                        client.send(msgResponse);
                                    } catch (RemoteException e) {
                                        e.printStackTrace();
                                    }
                                }},
                            icon_id);
                    break;
            }
        }
    }
}
