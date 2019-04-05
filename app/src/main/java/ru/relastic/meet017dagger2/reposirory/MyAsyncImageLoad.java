package ru.relastic.meet017dagger2.reposirory;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.inject.Inject;

import ru.relastic.meet017dagger2.domain.MyService;

public class MyAsyncImageLoad extends AsyncTask<String, Integer, Bitmap> implements ImageLoader{

    private static final String BASE_URL = "http://openweathermap.org/img/w/";
    private static final String FILE_EXT = ".png";

    private final Context mContext;
    private final File mDir;
    private MyService.IImageLoaderCallback mCallback;
    private String mIcon_id;
    private File mFile;


    public MyAsyncImageLoad(@NonNull Context context){
        mContext = context;
        mDir = context.getFilesDir();
    }

    private MyAsyncImageLoad buildExec(@NonNull MyService.IImageLoaderCallback callback, String icon_id) {

        this.mCallback = callback;
        this.mIcon_id = icon_id;
        String filename = icon_id+FILE_EXT;
        mFile = new File(mDir, filename);
        return this;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        mCallback.onLoadedImage(bitmap);
    }

    @Override
    protected Bitmap doInBackground(String... strings) {

        Bitmap retVal = getLocalStorage(mFile);
        if (retVal == null) {
            URL url = null;
            try {
                url = new URL(BASE_URL+mIcon_id+FILE_EXT);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            retVal = getRemoteStorage(url);
            if (retVal != null) {setLocalStorage(retVal,mFile);}
        }
        return retVal;
    }

    private Bitmap getLocalStorage(File file) {
        Bitmap retVal = null;
        FileInputStream fis = null;
        if (file.exists() && (file.length()>0)) {
            try {
                fis = new FileInputStream(file);
                retVal = BitmapFactory.decodeStream(fis);
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if (fis != null) {
                    try {fis.close();} catch (IOException e) {e.printStackTrace();}
                }
            }
        }
        return retVal;
    }
    private Bitmap getRemoteStorage(URL url) {
        HttpURLConnection connection = null;
        Bitmap retVal = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                retVal = BitmapFactory.decodeStream(connection.getInputStream());
            }
        } catch (IOException e) {
            //
        }finally {
            if(connection != null) {
                connection.disconnect();
            }
        }
        return retVal;
    }
    private void setLocalStorage(Bitmap bitmap, File file) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100,fos);
        } catch (IOException e) {
            try {
                fos.close();
                file.delete();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }finally {
            if(fos != null) {
                try {fos.close();} catch (IOException e) {e.printStackTrace();}
            }
        }
    }


    @Override
    public void loadImage(@NonNull MyService.IImageLoaderCallback callback, String icon_id) {
        MyAsyncImageLoad item = new MyAsyncImageLoad(mContext);
        item.buildExec(callback,icon_id).execute("");
    }
}
