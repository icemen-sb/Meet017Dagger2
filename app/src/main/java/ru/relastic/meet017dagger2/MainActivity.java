package ru.relastic.meet017dagger2;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.TreeMap;

import javax.inject.Inject;

import ru.relastic.meet017dagger2.dagger.Appl;
import ru.relastic.meet017dagger2.domain.WeatherEntity;
import ru.relastic.meet017dagger2.domain.WeatherEntityCurrent;
import ru.relastic.meet017dagger2.presenter.MyPresenter;
import ru.relastic.meet017dagger2.reposirory.RetrofitApiMapper;


public class MainActivity extends AppCompatActivity implements MyPresenter.IPreserterCallbacks{

    @Inject
    public MyPresenter mPresenter;

    private MyAdapter mAdapter;
    private MySpinnerAdapter mSpinnerAdapter;
    private RadioButton mRadioButtonToday;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Appl.getSubcomponent().inject(this);
        setContentView(R.layout.activity_main);
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
        mSpinnerAdapter = new MySpinnerAdapter((Spinner)findViewById(R.id.spinner));
        mRadioButtonToday = (RadioButton)findViewById(R.id.radioButtonToday);
    }

    private void initListeners() {
        mRadioButtonToday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mPresenter.getWeatherForecast(mSpinnerAdapter.getCityID(), mRadioButtonToday.isChecked());
            }
        });
    }

    private void init( ) {
        RecyclerView mRecyclerView = new RecyclerView(getApplicationContext());
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        WeatherEntity data = new WeatherEntity();
        mAdapter = new MyAdapter(data);
        mRecyclerView.setAdapter(mAdapter);
        ((ViewGroup)findViewById(R.id.main_frame_layout)).addView(mRecyclerView);
    }


    public void viewDetails(WeatherEntity data, int index) {
        startActivity(DetailsActivity.getIntent(MainActivity.this)
                .putExtra("data", RetrofitApiMapper.convertToString(data))
                .putExtra("index", index));
    }

    @Override
    public void onServiceConnected() {
        mPresenter.getWeatherForecast(mSpinnerAdapter.getCityID(),mRadioButtonToday.isChecked());
    }
    @Override
    public void onDataPrepared(WeatherEntity weatherEntity) {
        mAdapter.setData(weatherEntity);
    }
    @Override
    public void onItemPrepared(WeatherEntityCurrent weatherEntityCurrent) {

    }
    @Override
    public void onIconPrepared(Bitmap icon) {

    }

    class MySpinnerAdapter {
        private final String DEFAULT_CITY="Moscow";
        private final TreeMap<String, Long> mTreeMap = new TreeMap<>();
        Spinner mSpinner;
        long mCiti_id_current=0;
        MySpinnerAdapter(Spinner view) {
            mSpinner = view;

            mTreeMap.put("Kaliningrad", 554234L);
            mTreeMap.put("Moscow", 524901L);
            mTreeMap.put("St.Peterburg", 536203L);
            mTreeMap.put("Ykaterinburg", 1486209L);
            mTreeMap.put("Vladivostok", 2013348L);

            init();
        }

        private void init() {
            ArrayAdapter<Object> adapter = new ArrayAdapter<>(getApplicationContext(),
                    android.R.layout.simple_spinner_item,
                    mTreeMap.keySet().toArray());
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mSpinner.setAdapter(adapter);
            for(int i=0; i<mTreeMap.size();i++) {
                if (mSpinner.getItemAtPosition(i).toString().equals(DEFAULT_CITY)) {
                    mSpinner.setSelection(i);
                    break;
                }
            }

            mCiti_id_current = mTreeMap.get(DEFAULT_CITY);
            mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    mPresenter.getWeatherForecast(
                            mTreeMap.get(mSpinner.getItemAtPosition(position).toString()),
                            mRadioButtonToday.isChecked());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }

        public long getCityID() {
            return mCiti_id_current;
        }
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

        private WeatherEntity mData;

        MyAdapter(WeatherEntity data) {
            mData = (data == null) ? new WeatherEntity() : data;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new MyViewHolder(LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.rw_linear_layout,viewGroup,false));
        }

        @Override
        public void onBindViewHolder (@NonNull MyViewHolder myViewHolder, int i) {
            myViewHolder.mTextViewPos.setText(String.valueOf(i+1));
            myViewHolder.mTextViewDate.setText(mData.getList().get(i).getDateTimeString(null));
            myViewHolder.mTextViewDescription.setText("Температура "
                    + mData.getList().get(i).getMain().getTemp(null)
                    + '\n'+"(в диапазоне: " + mData.getList().get(i).getMain().getTemp_min(null)
                    + " - " + mData.getList().get(i).getMain().getTemp_max(null) + ")"
                    + '\n' + mData.getList().get(i).getWeather().get(0).getDescription());
            final ImageView iw = myViewHolder.mImageView;
            mPresenter.getBitmapByID(mData.getList().get(i).getWeather().get(0).getIcon_id(),
                    new MyPresenter.IPreserterImageCallback() {
                        @Override
                        public void onIconPrepared(Bitmap icon) {
                            iw.setImageBitmap(icon);
                        }
                    });
        }

        @Override
        public int getItemCount() {
            int retVal = 0;
            if (mData.getList() != null) {
                retVal = mData.getList().size();
            }
            return retVal;
        }

        public void setData(WeatherEntity data) {
            mData = data;
            notifyDataSetChanged();
        }

        public class MyViewHolder extends  RecyclerView.ViewHolder {
            public TextView mTextViewPos;
            public TextView mTextViewDate;
            public TextView mTextViewDescription;
            public ImageView mImageView;


            public MyViewHolder(View itemView){
                super(itemView);
                mTextViewPos =itemView.findViewById(R.id.textViewPos);
                mTextViewDate =itemView.findViewById(R.id.textViewDate);
                mTextViewDescription =itemView.findViewById(R.id.textViewDescription);
                mImageView =itemView.findViewById(R.id.imageView);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MainActivity.this.viewDetails(mData,getAdapterPosition());
                    }
                });
            }
        }
    }
}
