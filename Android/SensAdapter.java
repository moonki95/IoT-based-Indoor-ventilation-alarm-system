package com.example.arduino;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;


public class SensAdapter extends RecyclerView.Adapter<SensAdapter.CustomViewHolder> {

    private ArrayList<SensData> mList = null;
    private Activity context = null;


    public SensAdapter(Activity context, ArrayList<SensData> list) {
        this.context = context;
        this.mList = list;
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
       // protected TextView date;
        protected TextView temp;
        protected TextView humid;
        protected TextView dust;


        public CustomViewHolder(View view) {
            super(view);
           //this.date = (TextView) view.findViewById(R.id.textView_list_date);
            this.temp = (TextView) view.findViewById(R.id.textView_list_temp);
            this.humid = (TextView) view.findViewById(R.id.textView_list_humid);
            this.dust = (TextView) view.findViewById(R.id.textView_list_dust);

        }
    }


    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list, null);
        CustomViewHolder viewHolder = new CustomViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder viewholder, int position) {

       // viewholder.date.setText(mList.get(position).getDate());
        viewholder.temp.setText(mList.get(position).getTemp());
        viewholder.humid.setText(mList.get(position).getHumid());
        viewholder.dust.setText(mList.get(position).getDust());
    }

    @Override
    public int getItemCount() {
        return (null != mList ? mList.size() : 0);
    }

}