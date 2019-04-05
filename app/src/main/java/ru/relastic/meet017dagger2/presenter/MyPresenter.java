package ru.relastic.meet017dagger2.presenter;

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
import java.util.HashSet;

import javax.inject.Inject;

import ru.relastic.meet017dagger2.domain.MyService;
import ru.relastic.meet017dagger2.domain.WeatherEntity;
import ru.relastic.meet017dagger2.domain.WeatherEntityCurrent;

public class MyPresenter {
    private IPreserterCallbacks mIPreserterCallbacks;
    private final Context mContext;
    private final HashSet<IPreserterImageCallback> uiCallbacksArray = new HashSet<>();
    private MyHandler myHandler = new MyHandler();
    private Messenger mCurrent = new Messenger(myHandler);
    private Messenger mService;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = new Messenger(service);
            System.out.println("--------- SERVICE CONNECTED");
            mIPreserterCallbacks.onServiceConnected();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            System.out.println("--------- SERVICE DISCONNECTED");
            mService = null;
        }
    };

    public MyPresenter(@NonNull Context context) {
        this.mContext = context;
    }

    public boolean connect(IPreserterCallbacks callback){
        boolean valRetVal = (mService == null);
        mIPreserterCallbacks=callback;
        if (valRetVal) {mContext.bindService(
                MyService.getIntent(mContext),
                mConnection,
                Context.BIND_AUTO_CREATE);}
        return !valRetVal;
    }

    public void disconnect(){
        mIPreserterCallbacks=null;
        //mContext.unbindService(mConnection);
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
    public void getBitmapByID(@NonNull String iconID, IPreserterImageCallback callback) {
        Message msg = Message.obtain();
        msg.replyTo = mCurrent;
        msg.what = MyService.WHAT_REQUEST_ICON;
        msg.getData().putString(MyService.REQUEST_ICON_KEY,iconID);
        msg.getData().putInt(MyService.REQUEST_ICON_CALLBACK_KEY,callback.hashCode());
        try {
            mService.send(msg);
            uiCallbacksArray.add(callback);
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
                    mIPreserterCallbacks.onDataPrepared(WeatherEntity.prepareByArgs(msg));
                    break;
                case MyService.WHAT_MESSAGE_ITEM:
                    //
                    break;
                case MyService.WHAT_MESSAGE_ICON:
                    int hash_source = msg.getData().getInt(MyService.REQUEST_ICON_CALLBACK_KEY);
                    if (hash_source>0) {
                        for(IPreserterImageCallback item : uiCallbacksArray) {
                            if (item.hashCode()==hash_source) {
                                item.onIconPrepared((Bitmap) msg.obj);
                                uiCallbacksArray.remove(item);
                                break;
                            }
                        }
                    }else {
                        mIPreserterCallbacks.onIconPrepared((Bitmap) msg.obj);
                    }
                    break;
            }
        }
    }

    public interface IPreserterCallbacks {
        public void onServiceConnected();
        public void onDataPrepared(WeatherEntity weatherEntity);
        public void onItemPrepared(WeatherEntityCurrent weatherEntityCurrent);
        public void onIconPrepared(Bitmap icon);
    }
    public interface IPreserterImageCallback {
        public void onIconPrepared(Bitmap icon);
    }
}
