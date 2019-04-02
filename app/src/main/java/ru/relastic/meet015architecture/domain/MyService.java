package ru.relastic.meet015architecture.domain;

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

import retrofit2.Call;
import retrofit2.Response;
import ru.relastic.meet015architecture.reposiroty.RetrofitApiMapper;
import ru.relastic.meet015architecture.reposiroty.RetrofitHelper;
import ru.relastic.meet015architecture.reposiroty.MyAdapterAsyncImageLoad;

//Domain
public class MyService extends Service {
    public static final String REQUEST_CITY_KEY = "request_city_key";
    public static final String REQUEST_IS_TODAY_KEY = "request_is_today_key";
    public static final String REQUEST_ICON_KEY = "request_icon_key";
    public static final int WHAT_CLIENT_CONNECTED = 1;
    public static final int WHAT_MESSAGE_LIST = 2;
    public static final int WHAT_MESSAGE_ITEM = 3;
    public static final int WHAT_REQUEST_ITEM = 4;
    public static final int WHAT_REQUEST_LIST = 5;
    public static final int WHAT_REQUEST_ICON = 6;
    public static final int WHAT_MESSAGE_ICON = 7;


    private Messenger mService = new Messenger(new MyServiceHandler());


    @Override
    public IBinder onBind(Intent intent) {
        return mService.getBinder();
    }
    public static Intent getIntent(Context context){
        return new Intent(context, MyService.class);
    }


    class MyServiceHandler extends Handler {
        //Use cases

        private RetrofitApiMapper mRetrofitApiMapper = new RetrofitApiMapper(new RetrofitHelper());
        MyServiceHandler() {
            super(Looper.getMainLooper());
        }
        @Override
        public void handleMessage(Message msg) {
            final Messenger client = msg.replyTo;
            final Message msgResponse = new Message();
            msgResponse.copyFrom(msg);
            long id = msg.getData().getLong(REQUEST_CITY_KEY);
            switch (msg.what) {
                case WHAT_CLIENT_CONNECTED:
                    break;
                case WHAT_REQUEST_LIST:
                    mRetrofitApiMapper.requestWeatherByCitiId(id, RetrofitHelper.APPID, new retrofit2.Callback<WeatherEntity>() {
                        @Override
                        public void onResponse(Call<WeatherEntity> call, Response<WeatherEntity> response) {
                            msgResponse.what = WHAT_MESSAGE_LIST;
                            msgResponse.obj = response.body();
                            msgResponse.replyTo = mService;
                            try {
                                client.send(msgResponse);
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
