package ru.relastic.meet015architecture;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.widget.ImageView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;

public class MyService extends Service {
    public static final String REQUEST_CITY_KEY = "request_city_key";
    public static final String REQUEST_ICON_KEY = "request_icon_key";
    public static final int WHAT_CLIENT_CONNECTED = 1;
    public static final int WHAT_MESSAGE_LIST = 2;
    public static final int WHAT_MESSAGE_ITEM = 3;
    public static final int WHAT_REQUEST_ITEM = 4;
    public static final int WHAT_REQUEST_LIST = 5;
    public static final int WHAT_REQUEST_ICON = 6;
    public static final int WHAT_MESSAGE_ICON = 7;


    private Messenger mService = new Messenger(new MyServiceHandler());

    private RetrofitApiMapper mRetrofitApiMapper = new RetrofitApiMapper(new RetrofitHelper());

    public MyService() {
    }

    @Override
    public void onCreate() {
        mRetrofitApiMapper = new RetrofitApiMapper(new RetrofitHelper()) ;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mService.getBinder();
    }
    public static Intent getIntent(Context context){
        return new Intent(context, MyService.class);
    }


    class MyServiceHandler extends Handler {
        MyServiceHandler() {
            super(Looper.getMainLooper());
        }
        @Override
        public void handleMessage(Message msg) {

            final Messenger client = msg.replyTo;

            //long id= 554234L; //Kaliningrad
            //long id= 524901L; //Moscow
            //long id= 536203L; //St.Peterburg
            //long id= 1486209L; //Ykaterinburg
            //long id= 2013348L; //Vladivostok
            long id = msg.getData().getLong(REQUEST_CITY_KEY);
            if (id==0L) {id = 524901L;}
            switch (msg.what) {
                case WHAT_CLIENT_CONNECTED:
                    break;
                case WHAT_REQUEST_LIST:
                    mRetrofitApiMapper.requestWeatherByCitiId(id, RetrofitHelper.APPID, new retrofit2.Callback<WeatherEntity>() {
                        @Override
                        public void onResponse(Call<WeatherEntity> call, Response<WeatherEntity> response) {
                            Message msg = new Message();
                            msg.what = WHAT_MESSAGE_LIST;
                            msg.obj = response.body();
                            msg.replyTo = mService;
                            try {
                                client.send(msg);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<WeatherEntity> call, Throwable t) {
                            //
                        }
                    });
                    break;
                case WHAT_REQUEST_ITEM:
                    mRetrofitApiMapper.requestWeatherByCitiIdCurrent(id, RetrofitHelper.APPID, new retrofit2.Callback<WeatherEntityCurrent>() {

                        @Override
                        public void onResponse(Call<WeatherEntityCurrent> call, Response<WeatherEntityCurrent> response) {
                            Message msg = new Message();
                            msg.what = WHAT_MESSAGE_ITEM;
                            msg.obj = response.body();
                            msg.replyTo = mService;
                            try {
                                client.send(msg);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<WeatherEntityCurrent> call, Throwable t) {
                            //
                        }
                    });
                    break;
                case WHAT_REQUEST_ICON:
                    String icon_id = msg.getData().getString(REQUEST_ICON_KEY);
                    MyAdapterAsyncImageLoad myAdapterAsyncImageLoad = new MyAdapterAsyncImageLoad (getApplicationContext(),
                            null, icon_id);
                    myAdapterAsyncImageLoad.setOnloadedListener(new MyAdapterAsyncImageLoad.ILCallBacks() {
                        @Override
                        public void onLoadedImage(Bitmap bitmap) {
                            Message msg = new Message();
                            msg.what = WHAT_MESSAGE_ICON;
                            msg.obj = bitmap;
                            msg.replyTo = mService;
                            try {
                                client.send(msg);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    myAdapterAsyncImageLoad.execute("");
                    break;
            }
        }
    }


}
