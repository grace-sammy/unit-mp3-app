package com.example.sammy.mymp3player_thread_test;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

import static com.example.sammy.mymp3player_thread_test.SubAdapter.selectPosition;

public class SubActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    Button btnDelete, btnExit;

    SubAdapter subAdapter;
    LinearLayoutManager linearLayoutManager;
    ArrayList<MusicData> list = new ArrayList<>();

    MyDBHelper myDBHelper;
    SQLiteDatabase sqLiteDatabase;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.like_list);
        setTitle("찜 목록");

        recyclerView = findViewById(R.id.recyclerView);
        btnDelete = findViewById(R.id.btnDelete);
        btnExit = findViewById(R.id.btnExit);

        insertDBList();

        linearLayoutManager = new LinearLayoutManager(SubActivity.this);
        subAdapter = new SubAdapter(R.layout.list_item, list);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(subAdapter);

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDBHelper = new MyDBHelper(SubActivity.this);
                sqLiteDatabase = myDBHelper.getReadableDatabase();
                sqLiteDatabase.execSQL("DELETE FROM musicTBL WHERE id = "+list.get(selectPosition).getNum()+";");
                sqLiteDatabase.close();
                insertDBList();
                subAdapter.notifyDataSetChanged();
            }
        });
    }

    //조회하기
    private void insertDBList() {
        myDBHelper = new MyDBHelper(this);
        sqLiteDatabase = myDBHelper.getReadableDatabase();
        Cursor cursor;
        cursor = sqLiteDatabase.rawQuery("SELECT * FROM musicTBL;", null);
        //조회할 때 또 추가되서 나오지 않도록 하기 위해서 remove를 해줘야한다.
        list.removeAll(list);
                                            //sql id                                title                           singer                      albumIMG
        while (cursor.moveToNext()) {//     int num,                     String albumImg,                  String singer,               String musicTitle
            list.add(new MusicData(cursor.getInt(0),cursor.getString(3), cursor.getString(2), cursor.getString(1)));
        }

        cursor.close();
        sqLiteDatabase.close();
    }
}
