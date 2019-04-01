package ru.relastic.meet015architecture;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private static final int STORAGE_LOCAL = 0;
    private static final int STORAGE_NET = 1;

    private  final Context mContext;
    private WeatherEntity mData;
    private MyPresenter mPresenter;

    MyAdapter(Context context, WeatherEntity data, MyPresenter presenter) {
        mContext = context;
        mData = (data == null) ? new WeatherEntity() : data;
        mPresenter = presenter;
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
        Messenger messenger = new Messenger(new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MyService.WHAT_MESSAGE_ICON) {
                    iw.setImageBitmap((Bitmap)msg.obj);
                }
            }
        });
        Message message = new Message();
        message.replyTo = messenger;
        message.what = MyService.WHAT_REQUEST_ICON;
        message.getData().putString(MyService.REQUEST_ICON_KEY,
                mData.getList().get(i).getWeather().get(0).getIcon_id());
        try {
            mPresenter.getService().send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        int retVal = 0;
        if (mData.getList() != null) {retVal = mData.getList().size();}
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
                    ((MainActivity)mContext).viewDetails(mData,getAdapterPosition());
                }
            });
        }
    }
}
