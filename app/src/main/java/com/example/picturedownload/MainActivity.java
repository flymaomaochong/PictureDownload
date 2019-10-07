package com.example.picturedownload;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private String regx = "<img.*?src=\"(.*?)\"";
    //    private String regx = "<img class=\"main_img img-hover\".*?src=\"https:(.*?)\"";
    private String url = "http://www.tu11.com/shoujibizhi/";
    //    private String url = "https://image.baidu.com/search/index?tn=baiduimage&ct=201326592&lm=-1&cl=2&ie=gb18030&word=%CD%BC%BF%E2%B4%F3%C8%AB&fr=ala&ala=1&alatpl=adress&pos=0&hs=2&xthttps=111111";
    private TextView tv_url;
    private TextView tv_regx;
    private RecyclerView recyclerview;
    private List<DataBean> datas = new ArrayList<>();
    private ImageView iv_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iv_image = (ImageView) findViewById(R.id.iv_image);
        tv_url = (TextView) findViewById(R.id.tv_url);
        tv_regx = (TextView) findViewById(R.id.tv_regx);
        recyclerview = (RecyclerView) findViewById(R.id.recyclerview);
        tv_regx.setText("正则表达式：" + regx);


//        Glide.with(MainActivity.this).load("http://img11.tu11.com:8080/uploads/allimg/c180829/15354P059593P-195Q8_lit.jpg").into(iv_image);
        getData();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayout.VERTICAL);
        recyclerview.setLayoutManager(linearLayoutManager);

    }


    private void getData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient okHttpClient1 = new OkHttpClient();
                Request request1=new Request.Builder().url("http://img11.tu11.com:8080/uploads/allimg/c180829/15354P059593P-195Q8_lit.jpg").build();
//                Request request1 = new Request.Builder().url("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1570444002069&di=2d23b9c92b02ffc7b073a77f2b347412&imgtype=0&src=http%3A%2F%2Fpic.16pic.com%2F00%2F23%2F97%2F16pic_2397078_b.jpg").build();
                try (Response response1 = okHttpClient1.newCall(request1).execute()) {
                    final Bitmap bitmap = BitmapFactory.decodeStream(response1.body().byteStream());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            iv_image.setImageBitmap(bitmap);
                        }
                    });

                } catch (Exception ex) {
                    Log.i("这里是错误", ex.toString());
                }


                OkHttpClient okHttpClient = new OkHttpClient();
                Request request = new Request.Builder().url(url).build();
                try (Response response = okHttpClient.newCall(request).execute()) {
                    String result = response.body().string();

                    Pattern pattern = Pattern.compile(regx);
                    Matcher matcher = pattern.matcher(result);
                    while (matcher.find()) {
                        String name = matcher.group(1);
                        Log.i("这里是最终", name);
                        datas.add(new DataBean(name));
//                        download(name);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            recyclerview.setAdapter(new MyRecyclerAdapter(MainActivity.this, datas));
                        }
                    });

                } catch (Exception ex) {
                    Log.i("这里是最终", ex.toString());
                }
            }
        }).start();

    }

    private void download(final String name) {

        Log.i("这里是123", name);
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient okHttpClient = new OkHttpClient();
                Request request = new Request.Builder().url(name).build();
                try (Response response = okHttpClient.newCall(request).execute()) {
                    InputStream inputStream = response.body().byteStream();
                    final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    final ImageView imageView = new ImageView(getApplicationContext(), null, 100, 100);
                    imageView.setImageBitmap(bitmap);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                        }
                    });

                } catch (Exception ex) {
                    Log.i("这里是", ex.toString());
                }
            }
        }).start();

    }
}
