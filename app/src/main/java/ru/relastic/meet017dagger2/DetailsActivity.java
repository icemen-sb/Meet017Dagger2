package ru.relastic.meet017dagger2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import javax.inject.Inject;

import ru.relastic.meet017dagger2.dagger.Appl;
import ru.relastic.meet017dagger2.domain.WeatherEntity;
import ru.relastic.meet017dagger2.domain.WeatherEntityCurrent;
import ru.relastic.meet017dagger2.presenter.MyPresenter;
import ru.relastic.meet017dagger2.reposirory.RetrofitApiMapper;


public class DetailsActivity extends AppCompatActivity implements MyPresenter.IPreserterCallbacks {
    WeatherEntity mData;
    int index;
    Button mButton;
    ImageView mImageView;
    TextView mTWCity, mTWDate, mTWTime, mTWLat, mTWLon,
             mTW_par_left1, mTW_par_left2, mTW_par_left3, mTW_par_left4, mTW_par_left5
            , mTW_par_left6, mTW_par_left7,
             mTW_par_right1, mTW_par_right2, mTW_par_right3, mTW_par_right4, mTW_par_right5
            , mTW_par_right6, mTW_par_right7;

    @Inject
    public MyPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Appl.getSubcomponent().inject(this);

        setContentView(R.layout.activity_details);

        mData = RetrofitApiMapper.convertJSON(getIntent().getExtras().getString("data"));
        index = getIntent().getExtras().getInt("index");

        initViews();
        initListeners();
        init();
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (mPresenter.connect(this)) {
            onServiceConnected();
        }
    }
    @Override
    protected void onPause() {
        mPresenter.disconnect();
        super.onPause();
    }

    private void initViews() {
        mButton = findViewById(R.id.button);
        mImageView = findViewById(R.id.details_image_view);
        mTWCity = findViewById(R.id.details_textViewCity);
        mTWDate = findViewById(R.id.details_textViewDate);
        mTWLat = findViewById(R.id.details_textViewLat);
        mTWLon = findViewById(R.id.details_textViewLon);
        mTW_par_left1 = findViewById(R.id.details_txt_left1);
        mTW_par_left2 = findViewById(R.id.details_txt_left2);
        mTW_par_left3 = findViewById(R.id.details_txt_left3);
        mTW_par_left4 = findViewById(R.id.details_txt_left4);
        mTW_par_left5 = findViewById(R.id.details_txt_left5);
        mTW_par_left6 = findViewById(R.id.details_txt_left6);
        mTW_par_left7 = findViewById(R.id.details_txt_left7);
        mTW_par_right1 = findViewById(R.id.details_txt_right1);
        mTW_par_right2 = findViewById(R.id.details_txt_right2);
        mTW_par_right3 = findViewById(R.id.details_txt_right3);
        mTW_par_right4 = findViewById(R.id.details_txt_right4);
        mTW_par_right5 = findViewById(R.id.details_txt_right5);
        mTW_par_right6 = findViewById(R.id.details_txt_right6);
        mTW_par_right7 = findViewById(R.id.details_txt_right7);
}

    private void initListeners() {
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void init() {
        mTWCity.setText(mData.getCity().getName());
        mTWDate.setText(mData.getList().get(index).getDateTimeString(null));
        mTWLat.setText(String.valueOf(mData.getCity().getCoord().getLat()));
        mTWLon.setText(String.valueOf(mData.getCity().getCoord().getLon()));
        mTW_par_left1.setText("Температура");
        mTW_par_right1.setText(String.valueOf(mData.getList().get(index).getMain().getTemp(null)));
        mTW_par_left2.setText("Температура мин");
        mTW_par_right2.setText(String.valueOf(mData.getList().get(index).getMain().getTemp_min(null)));
        mTW_par_left3.setText("Температура макс");
        mTW_par_right3.setText(String.valueOf(mData.getList().get(index).getMain().getTemp_max(null)));
        mTW_par_left4.setText("Давление");
        mTW_par_right4.setText(String.valueOf(mData.getList().get(index).getMain().getPressure()));
        mTW_par_left5.setText("Влажность");
        mTW_par_right5.setText(String.valueOf(mData.getList().get(index).getMain().getHumidity()));
        mTW_par_left6.setText("Скорость ветра");
        mTW_par_right6.setText(String.valueOf(mData.getList().get(index).getWind().getSpeed()));
        mTW_par_left7.setText("Облачность");
        mTW_par_right7.setText(String.valueOf(mData.getList().get(index).getClouds().getAll()));
    }

    public static Intent getIntent(Context context){
        return new Intent(context, DetailsActivity.class);
    }

    @Override
    public void onServiceConnected() {
        mPresenter.getBitmapByID(mData.getList().get(index).getWeather().get(0).getIcon_id());
    }
    @Override
    public void onDataPrepared(WeatherEntity weatherEntity) {}
    @Override
    public void onItemPrepared(WeatherEntityCurrent weatherEntityCurrent) {}
    @Override
    public void onIconPrepared(Bitmap icon) {
        mImageView.setImageBitmap(icon);
    }
}
