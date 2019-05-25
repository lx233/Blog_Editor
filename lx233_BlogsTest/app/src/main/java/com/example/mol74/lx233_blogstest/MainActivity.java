package com.example.mol74.lx233_blogstest;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import android.widget.Toast;

import medusa.theone.waterdroplistview.view.WaterDropListView;

public class MainActivity extends AppCompatActivity {
    public static MyDatabaseHelper dbHelper;
    public SQLiteDatabase db;
    private WaterDropListView listview;
    private String _id_;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //每次进入程序都会对列表进行自动刷新
        dbHelper = new MyDatabaseHelper(this, "Blogs.db", null, 1);
        listview = (WaterDropListView) findViewById(R.id.water_blog_list);
        refresh();

        //新增博客的活动跳转
        Button add_blog = (Button) findViewById(R.id.add_blog);
        add_blog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                intent.putExtra("ifnew", true);
                startActivity(intent);
            }
        });

        //单击每条可以进入具体查看界面
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView listView = (ListView) parent;
                HashMap<String, Object> map = (HashMap<String, Object>) listView.getItemAtPosition(position);
                if (id == -1) {
                    // todo：这里可以再写一个,如果多于10条的时候怎么样怎么样.... 现在只是简单处理了一下
                    Toast.makeText(MainActivity.this, "已无更多~ ", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(MainActivity.this, CheckActivity.class);
                    intent.putExtra("_id_", map.get("id").toString());
                    intent.putExtra("ifnew", false);
                    startActivity(intent);
                }
            }
        });

        //长按会删除...
        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                db = dbHelper.getWritableDatabase();
                ListView listView = (ListView) parent;
                HashMap<String, Object> map = (HashMap<String, Object>) listView.getItemAtPosition(position);
                _id_ = map.get("id").toString();
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("提示");
                builder.setMessage("确定要删除吗？\n删了就没了！！！！！");
                builder.setPositiveButton("删了删了", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        db.delete("Blogs", "id = ?", new String[]{_id_});
                        refresh();
                        Toast.makeText(MainActivity.this, "删除成功:(", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.show();
                return true;
            }
        });

        //引用外部工程 实现下拉刷新效果
        listview.setWaterDropListViewListener(new WaterDropListView.IWaterDropListViewListener() {
            @Override
            public void onRefresh() {
                refresh();
                listview.stopRefresh();//停止刷新
            }

            public void onLoadMore() {
                System.out.println("waterDropListView 加载更多开始开始");
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("提示");
                builder.setMessage("已无更多内容~");
                builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.show();
                listview.stopLoadMore();//停止加载更多
            }
        });
    }

    //刷新方法。每次刷新会重新查询 todo ：查询前几条
    private void refresh() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("Blogs", null, null, null, null, null, null);
        ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String, String>>();
        arrayList.clear();
        if (cursor.moveToFirst()) {
            do {
                // 遍历Cursor对象，取出数据
                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.clear();
                String id = cursor.getString(cursor.getColumnIndex("id"));
                String date = cursor.getString(cursor.getColumnIndex("date"));
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String preview = cursor.getString(cursor.getColumnIndex("preview"));
                hashMap.put("id", id);
                hashMap.put("title", title);
                hashMap.put("preview", preview);
                hashMap.put("date", date);
                arrayList.add(hashMap);
                //cursor.move 移动到指定位 可以按每次10条进行读取..
            } while (cursor.moveToNext());
        } else {
            Log.d("MainActivity", "查询不到数据");
        }
        TextView text_title = (TextView) findViewById(R.id.text_title);
        text_title.setText("全部博客");

        // 将ArrayList中的元素进行倒序,最新的最先查看。这是按照id查询，也可按照时间啊什么的
        Collections.reverse(arrayList);
        cursor.close();
        SimpleAdapter adapter = new SimpleAdapter(MainActivity.this, arrayList, R.layout.simple_item_for_list, new String[]{"title", "preview", "date"}, new int[]{R.id.stitle, R.id.spreview, R.id.sdate});
        listview.setAdapter(adapter);
    }
}
