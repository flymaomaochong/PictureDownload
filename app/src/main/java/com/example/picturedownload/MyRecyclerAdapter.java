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

    public MyRecyclerAdapter(Context context, List<DataBean> datas) {
        this.context = context;
        this.datas = datas;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_image, null);
        MyViewHolder viewHolder = new MyViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        Log.i("这里是图片的地址显示", datas.get(i).getUrl());
//        Glide.with(context).load(datas.get(i).getUrl()).placeholder(R.mipmap.ic_launcher_round).error(R.mipmap.ic_launcher).into(myViewHolder.image);
        myViewHolder.tv_title.setText("这是第"+i+"个");
//        Glide.with(context).load("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1570444002069&di=2d23b9c92b02ffc7b073a77f2b347412&imgtype=0&src=http%3A%2F%2Fpic.16pic.com%2F00%2F23%2F97%2F16pic_2397078_b.jpg").into(myViewHolder.image);
//        Glide.with(context).load("http://img11.tu11.com:8080/uploads/allimg/c180829/15354P059593P-195Q8_lit.jpg").into(myViewHolder.image);

        Glide.with(context).load("http://img11.tu11.com:8080/uploads/allimg/c180829/15354P059593P-195Q8_lit.jpg").into(myViewHolder.image);

    }

    @Override
    public int getItemCount() {
//        return datas.size();
        return 3;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView tv_title;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
        }
    }
}
