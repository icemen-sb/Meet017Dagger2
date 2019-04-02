package ru.relastic.meet015architecture.presenter;

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

import ru.relastic.meet015architecture.domain.MyService;
import ru.relastic.meet015architecture.domain.WeatherEntity;
import ru.relastic.meet015architecture.domain.WeatherEntityCurrent;

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

    public MyPresenter(MPreserterCallbacks callback) {
        context = (Context)callback;
        uiComponent = callback;
        context.bindService(MyService.getIntent(context), mConnection, Context.BIND_AUTO_CREATE);
    }


    public void getWeatherForecast(@NonNull Long citi_id, @NonNull boolean isToday) {
        if (mService != null) {
            Message msg = Message.obtain();
            msg.replyTo = mCurrent;
            msg.what = MyService.WHAT_REQUEST_LIST;
            msg.getData().putLong(MyService.REQUEST_CITY_KEY,citi_id);
            msg.getData().putBoolean(MyService.REQUEST_IS_TODAY_KEY, isToday);
            try {
                mService.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
    public void getCurrentWeatherForecast(@NonNull Long citi_id) {
        if (mService != null) {
            Message msg = Message.obtain();
            msg.replyTo = mCurrent;
            msg.what = MyService.WHAT_REQUEST_ITEM;
            msg.getData().putLong(MyService.REQUEST_CITY_KEY, citi_id);
            try {
                mService.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
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
            switch (msg.what) {
                case MyService.WHAT_MESSAGE_LIST:
                    uiComponent.onDataPrepared(WeatherEntity.prepareByArgs(msg));
                    break;
                case MyService.WHAT_MESSAGE_ITEM:
                    //uiComponent.onDataPrepared(WeatherEntity.prepareByArgs(msg));
                    break;
                case MyService.WHAT_MESSAGE_ICON:
                    break;
            }
        }
    }

    public Messenger getService(){
        return mService;
    }
    public interface MPreserterCallbacks {
        public void onServiceConnected();
        public void onDataPrepared(WeatherEntity weatherEntity);
        public void onItemPrepared(WeatherEntityCurrent weatherEntityCurrent);
        public void onIconPrepared(Bitmap icon, int pos);
    }
}
