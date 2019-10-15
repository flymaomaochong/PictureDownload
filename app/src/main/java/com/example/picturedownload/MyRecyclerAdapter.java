package com.example.picturedownload;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by sxj on 2019/10/7.
 */
public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.MyViewHolder> {
    private List<DataBean> datas;
    private Context context;
    private ItemClick mItemClick;

    public MyRecyclerAdapter(Context context, List<DataBean> datas) {
        this.context = context;
        this.datas = datas;
    }

    public void click(ItemClick mItemClick) {
        this.mItemClick = mItemClick;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_image, null);
        MyViewHolder viewHolder = new MyViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {
        Glide.with(context).load(datas.get(i).getNetUrl()).into(myViewHolder.image);
        myViewHolder.tv_title.setText("这是第" + i + "个");
        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mItemClick != null) {
                    mItemClick.onItemClick(i);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public interface ItemClick {
        void onItemClick(int positon);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView tv_title;
        public View itemView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            image = (ImageView) itemView.findViewById(R.id.image);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
        }
    }
}
