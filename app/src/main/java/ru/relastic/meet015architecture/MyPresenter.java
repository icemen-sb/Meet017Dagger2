package ru.relastic.meet015architecture;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class MyPresenter {

    private MPreserterCallbacks uiComponent;
    private Context context;
    private MyHandler myHandler = new MyHandler();
    private Messenger mCurrent = new Messenger(myHandler);
    private Messenger mService;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = new Messenger(service);
            uiComponent.onServiceConnected();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }
    };

    MyPresenter(MPreserterCallbacks callback) {
        context = (Context)callback;
        uiComponent = callback;
        context.bindService(MyService.getIntent(context), mConnection, Context.BIND_AUTO_CREATE);
    }


    public void getWeatherForecast(@Nullable Long citi_id) {
        Message msg = Message.obtain();
        msg.replyTo = mCurrent;
        msg.what = MyService.WHAT_REQUEST_LIST;
        msg.getData().putLong(MyService.REQUEST_CITY_KEY,554234L);
        try {
            mService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    public void getCurrentWeatherForecast(@Nullable Long citi_id) {
        Message msg = Message.obtain();
        msg.replyTo = mCurrent;
        msg.what = MyService.WHAT_MESSAGE_ITEM;
        msg.getData().putLong(MyService.REQUEST_CITY_KEY,citi_id);
        try {
            mService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    public void getBitmapByID(@NonNull String iconID) {
        Message msg = Message.obtain();
        msg.replyTo = mCurrent;
        msg.what = MyService.WHAT_REQUEST_ICON;
        msg.getData().putString(MyService.REQUEST_ICON_KEY,iconID);
        try {
            mService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    class MyHandler extends Handler {
        MyHandler() {
            super(Looper.getMainLooper());
        }
        @Override
        public void handleMessage(Message msg) {
            WeatherEntity data_list;
            WeatherEntityCurrent data_list_item;
            Bitmap icon;
            int pos;
            switch (msg.what) {
                case MyService.WHAT_MESSAGE_LIST:
                    data_list = (WeatherEntity)msg.obj;
                    uiComponent.onDataPrepared(data_list);
                    break;
                case MyService.WHAT_MESSAGE_ITEM:
                    data_list_item = (WeatherEntityCurrent)msg.obj;
                    uiComponent.onItemPrepared(data_list_item);
                    break;
                case MyService.WHAT_MESSAGE_ICON:


                    System.out.println("-------------------------- HAS WHAT_MESSAGE_ICON IN MYPRESENTER " +
                            (msg.obj==null));
                    break;
            }
        }
    }
    public Messenger getService(){
        return mService;
    }
    interface MPreserterCallbacks {
        public void onServiceConnected();
        public void onDataPrepared(WeatherEntity weatherEntity);
        public void onItemPrepared(WeatherEntityCurrent weatherEntityCurrent);
        public void onIconPrepared(Bitmap icon, int pos);
    }
}
