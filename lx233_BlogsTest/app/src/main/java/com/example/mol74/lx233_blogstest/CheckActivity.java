package com.example.mol74.lx233_blogstest;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.scrat.app.richtext.RichEditText;

import java.util.ArrayList;
import java.util.HashMap;

import static com.example.mol74.lx233_blogstest.MainActivity.dbHelper;

public class CheckActivity extends AppCompatActivity {

    private String _id_;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);
        check_refresh();

        //进行编辑的 活动跳转
        Button edit = (Button) findViewById(R.id.to_edit);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CheckActivity.this, EditActivity.class);
                intent.putExtra("ifnew", false);
                intent.putExtra("_id_", _id_);
                startActivity(intent);
                check_refresh();
            }
        });

        //返回按钮(回到主程序)
        Button back = (Button) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CheckActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    //刷新具体代码，每次刷新会进行查询
    public void check_refresh() {
        Intent intent = getIntent();
        _id_ = intent.getStringExtra("_id_");
        Log.d("check", _id_);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("Blogs", null, "id = ?", new String[]{_id_}, null, null, null);
        ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String, String>>();
        if (cursor.moveToFirst()) {
            String ans_title = cursor.getString(cursor.getColumnIndex("title"));
            String ans_date = cursor.getString(cursor.getColumnIndex("date"));
            String ans_passage = cursor.getString(cursor.getColumnIndex("passage"));
            TextView t_title = (TextView) findViewById(R.id.t_title);
            t_title.setText(ans_title);
            TextView t_date = (TextView) findViewById(R.id.t_date);
            t_date.setText(ans_date);
            RichEditText t_passage = (RichEditText) findViewById(R.id.t_passage);
            t_passage.fromHtml(ans_passage);
        } else {
            Log.d("CheckActivty :", "找不到这个方法");
        }
        cursor.close();
        //此处有处理隐藏软键盘
    }
}

