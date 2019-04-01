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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MyPresenter.MPreserterCallbacks{

    private MyAdapter mAdapter;
    private MyPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        initListeners();
        init();
    }
    private void initViews() {
    }

    private void initListeners() {
    }

    private void init( ) {
        mPresenter = new MyPresenter(this);
        RecyclerView mRecyclerView = new RecyclerView(getApplicationContext());
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        WeatherEntity data = createData();
        mAdapter = new MyAdapter(this, data, mPresenter);
        mRecyclerView.setAdapter(mAdapter);
        ((ViewGroup)findViewById(R.id.main_frame_layout)).addView(mRecyclerView);
    }

    private WeatherEntity createData() {
        return new WeatherEntity();
    }


    public void viewDetails(WeatherEntity data, int index) {
        startActivity(DetailsActivity.getIntent(MainActivity.this)
                .putExtra("data", RetrofitApiMapper.convertToString(data))
                .putExtra("index", index));
    }

    @Override
    public void onServiceConnected() {
        mPresenter.getWeatherForecast(null);
    }

    @Override
    public void onDataPrepared(WeatherEntity weatherEntity) {
        mAdapter.setData(weatherEntity);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemPrepared(WeatherEntityCurrent weatherEntityCurrent) {

    }
    @Override
    public void onIconPrepared(Bitmap icon, int pos) {

    }
}
