package com.example.picturedownload;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.picturedownload.data.MyHelper;

import java.util.ArrayList;
import java.util.List;

public class SecondActivity extends AppCompatActivity {

    private RecyclerView recyclerview;
    private MyRecyclerAdapter myadapter;
    private List<DataBean> datas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        recyclerview = (RecyclerView) findViewById(R.id.recyclerview);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(SecondActivity.this, 3);
        recyclerview.setLayoutManager(gridLayoutManager);

        SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(getDatabasePath("pic.db"), null);
        Cursor cursor = sqLiteDatabase.query("picInfo", null, null, null, null, null, null);
        cursor.moveToFirst();
        datas = new ArrayList<>();
        do {
            String neturl = cursor.getString(cursor.getColumnIndex("neturl"));
            String localurl = cursor.getString(cursor.getColumnIndex("localurl"));
            DataBean dataBean = new DataBean(neturl, localurl);
            datas.add(dataBean);
        }while (cursor.moveToNext());
        cursor.close();

        myadapter = new MyRecyclerAdapter(SecondActivity.this, datas);
        recyclerview.setAdapter(myadapter);


    }
}
