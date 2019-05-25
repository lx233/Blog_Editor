package com.example.mol74.lx233_blogstest;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    //建库语句
    public static final String CREATE_ = "create table Blogs ("
            + "id integer primary key autoincrement, "
            + "date text, "
            + "title text, "
            + "preview text,"
            + "passage text)";
    private Context mContext;

    //构造函数
    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_);
        Log.d("MyDatabaseHelper", "Create succeeded");
        //Toast.makeText(mContext, "Create succeeded", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}