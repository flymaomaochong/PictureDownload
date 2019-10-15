package com.example.picturedownload;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.picturedownload.data.MyHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private String regx = "<img.*?data-original=\"(http.*?)\"";
    private String url = "http://699pic.com/?sem=1&sem_kid=46079&sem_type=1&b_scene_zt=1";
    private final String PATH_DIR = Environment.getExternalStorageDirectory() + File.separator + "imgs";
    private TextView tv_url;
    private TextView tv_regx;
    private RecyclerView recyclerview;
    private List<DataBean> datas = new ArrayList<>();
    private Set<String>localname=new HashSet<>();
    private HashMap<String,Integer>names=new HashMap<>();
    private TextView tv_create;
    private SQLiteDatabase sqLiteDatabase;
    private TextView tv_add;
    private TextView tv_delete;
    private TextView tv_download;
    private AtomicInteger count = new AtomicInteger(0);;
    private ProgressBar progress;
    private MyRecyclerAdapter myadapter;
    private TextView tv_reload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_download = (TextView) findViewById(R.id.tv_download);
        tv_create = (TextView) findViewById(R.id.tv_create);
        tv_delete = (TextView) findViewById(R.id.tv_delete);
        tv_add = (TextView) findViewById(R.id.tv_add);
        tv_url = (TextView) findViewById(R.id.tv_url);
        tv_regx = (TextView) findViewById(R.id.tv_regx);
        progress =(ProgressBar)findViewById(R.id.progress);
        tv_reload =(TextView)findViewById(R.id.tv_reload);
        progress.setVisibility(View.GONE);
        recyclerview = (RecyclerView) findViewById(R.id.recyclerview);
        tv_regx.setText("正则表达式：" + regx);
        tv_reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,SecondActivity.class));
            }
        });

//        Glide.with(MainActivity.this).load("http://img11.tu11.com:8080/uploads/allimg/c180829/15354P059593P-195Q8_lit.jpg").into(iv_image);
        getData();
        tv_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                download(datas);
            }
        });
        tv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sqLiteDatabase.execSQL("delete from picInfo");
            }
        });
        tv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues contentValues = new ContentValues();
                sqLiteDatabase.execSQL("delete from picInfo");
                for (DataBean itemData : datas) {
                    contentValues.put("neturl", itemData.getNetUrl());
                    contentValues.put("localurl", itemData.getLocalUrl());
                    sqLiteDatabase.insert("picInfo", null, contentValues);
                }
            }
        });
        tv_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyHelper myHelper = new MyHelper(MainActivity.this, 1);
                sqLiteDatabase = myHelper.getWritableDatabase();
            }
        });
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
//        linearLayoutManager.setOrientation(LinearLayout.VERTICAL);
//        recyclerview.setLayoutManager(linearLayoutManager);


        GridLayoutManager gridLayoutManager = new GridLayoutManager(MainActivity.this, 3);
        recyclerview.setLayoutManager(gridLayoutManager);
        myadapter = new MyRecyclerAdapter(MainActivity.this, datas);
        recyclerview.setAdapter(myadapter);
        myadapter.click(new MyRecyclerAdapter.ItemClick() {
            @Override
            public void onItemClick(int positon) {
                download(datas.get(positon));
            }
        });
    }


    private void getData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient okHttpClient = new OkHttpClient();
                Request request = new Request.Builder().url(url).build();
                try (Response response = okHttpClient.newCall(request).execute()) {
                    String result = response.body().string();
                    Pattern pattern = Pattern.compile(regx);
                    Matcher matcher = pattern.matcher(result);
                    while (matcher.find()) {
                        String name = matcher.group(1);
                        Log.i("这里是最终", name);
                        String local_name=parse2md5(name)+".png";
                        datas.add(new DataBean(name, local_name));
                        localname.add(local_name);
                        if(names.containsKey(local_name)){
                            names.put(local_name,names.get(local_name)+1);
                        }else {
                            names.put(local_name,1);
                        }

//                        download(name);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            int count=0;
                           for (Integer item:names.values()){
                               count+=item;
                               Log.i("图片的下载数量",item+"");
                           }
                            Log.i("图片的下载数量合计",count+"？？"+datas.size());
                            Toast.makeText(MainActivity.this, localname.size()+"条",Toast.LENGTH_SHORT).show();
                           myadapter.notifyDataSetChanged();
                        }
                    });

                } catch (Exception ex) {
                    Log.i("这里是最终", ex.toString());
                }
            }
        }).start();

    }
/**
 * 多文件
 * */
    private void download(final List<DataBean> datas) {
        Log.i("这里是图片下载数量",datas.size()+"");
        progress.setVisibility(View.VISIBLE);
        progress.setMax(datas.size());
        File dir = new File(PATH_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        //创建基本线程池

//        final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(3, 5, 1, TimeUnit.SECONDS,
//                new LinkedBlockingQueue<Runnable>(10));
        final OkHttpClient okHttpClient = new OkHttpClient();

        new Thread(new Runnable() {
            @Override
            public void run() {
                InputStream inputStream=null;
                FileOutputStream fileOutputStream=null;
                for (int i=0;i<datas.size();i++) {
                    final DataBean item=datas.get(i);
                            Log.i("这里是图片下载下标",count.incrementAndGet()+"");
                            Request request = new Request.Builder().url(item.getNetUrl()).build();
                            try (Response response = okHttpClient.newCall(request).execute()) {
                                 inputStream = response.body().byteStream();
                                File file = new File(PATH_DIR, item.getLocalUrl());
                                if (file.exists()) {
                                    file.delete();
                                }
                                file.createNewFile();
                                 fileOutputStream = new FileOutputStream(file);
                                int len = 0;
                                while ((len = inputStream.read()) != -1) {
                                    fileOutputStream.write(len);
                                }
                                fileOutputStream.close();
                                inputStream.close();
                            } catch (Exception ex) {
                                Log.i("这里是", ex.toString());
                            }finally {

                                progress.setProgress(0);
                            }
                    progress.setProgress(i);
                        }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progress.setVisibility(View.GONE);
                    }
                });
                    };
                }).start();

        Toast.makeText(MainActivity.this, "下载完成,数量"+count.get(), Toast.LENGTH_SHORT).show();
    }
    /**
     * 单个文件
     * */
    private void download(final DataBean dataBean) {
        progress.setVisibility(View.VISIBLE);
        progress.setMax(100);
        File dir = new File(PATH_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        //创建基本线程池

//        final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(3, 5, 1, TimeUnit.SECONDS,
//                new LinkedBlockingQueue<Runnable>(10));
        final OkHttpClient okHttpClient = new OkHttpClient();

        new Thread(new Runnable() {
            @Override
            public void run() {
                InputStream inputStream=null;
                FileOutputStream fileOutputStream=null;
//                for (int i=0;i<datas.size();i++) {
//                    final DataBean item=datas.get(i);
                    Log.i("这里是图片下载下标",count.incrementAndGet()+"");
                    Request request = new Request.Builder().url(dataBean.getNetUrl()).build();
                    try (Response response = okHttpClient.newCall(request).execute()) {
                        inputStream = response.body().byteStream();
                        File file = new File(PATH_DIR, dataBean.getLocalUrl());
//                        if (file.exists()) {
//                            file.delete();
//                        }
                        if (!file.exists()) {
                            file.createNewFile();
                        }
                        fileOutputStream = new FileOutputStream(file);
                        int len = 0;
                        while ((len = inputStream.read()) != -1) {
                            fileOutputStream.write(len);
                        }
                        fileOutputStream.close();
                        inputStream.close();
                    } catch (Exception ex) {
                        Log.i("这里是", ex.toString());
                    }finally {

                        progress.setProgress(0);
                    }
                    progress.setProgress(100);
//                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progress.setVisibility(View.GONE);
                    }
                });
            };
        }).start();

        Toast.makeText(MainActivity.this, "下载完成,数量"+count.get(), Toast.LENGTH_SHORT).show();
    }

    /**
     * 将图片的网址转换成MD5格式，这样就不会命名重复，而且还会符合文件命名规范，
     * 因为直接将网址作为名称含有分号等特殊字符，不符合命名规范
     * @param origin 图片或文件的网络地址
     * */
    private String parse2md5(String origin){
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
            return "";
        }
        char[] charArray = origin.toCharArray();
        byte[] byteArray = new byte[charArray.length];

        for (int i = 0; i < charArray.length; i++)
            byteArray[i] = (byte) charArray[i];
        byte[] md5Bytes = md5.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16)
                hexValue.append("0");
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();

    }
}
