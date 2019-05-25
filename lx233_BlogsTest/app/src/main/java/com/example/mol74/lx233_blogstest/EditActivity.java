package com.example.mol74.lx233_blogstest;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.scrat.app.richtext.RichEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import medusa.theone.waterdroplistview.activity.MainActivity;

public class EditActivity extends AppCompatActivity {
    public MyDatabaseHelper dbHelper;
    private EditText title;
    private RichEditText richEditText;
    private boolean isnew;
    private Intent intent;
    private String rowid;
    public static final int CHOOSE_PHOTO = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        dbHelper = new MyDatabaseHelper(this, "Blogs.db", null, 1);
        title = (EditText) findViewById(R.id.title);
        richEditText = (RichEditText) findViewById(R.id.rich_text);

        //如果是进行编辑，则获取之前的部分，在此数据基础上进行。（由main的点击事件来到这里） 否则，就按照默认进行新建
        intent = getIntent();
        isnew = intent.getBooleanExtra("ifnew", true);
        if (isnew == false) {
            String _id_ = intent.getStringExtra("_id_");
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            Cursor cursor = db.query("Blogs", null, "id = ?", new String[]{_id_}, null, null, null);
            ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String, String>>();
            if (cursor.moveToFirst()) {
                String ans_title = cursor.getString(cursor.getColumnIndex("title"));
                String ans_passage = cursor.getString(cursor.getColumnIndex("passage"));
                title.setText(ans_title);
                richEditText.fromHtml(ans_passage);
            } else {
                Log.d("EditActivty :", "找不到这个方法");
            }
            cursor.close();
        }


        Button back = (Button) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);
                builder.setTitle("提示");
                builder.setMessage("现在退出，未保存的将无法应用于更改");
                builder.setNegativeButton("执意退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                });
                builder.setPositiveButton("继续编辑", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.show();
            }
        });

        // 下面是编辑区的具体代码
        ImageButton undo = (ImageButton) findViewById(R.id.undo);
        undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                richEditText.undo();
            }
        });

        ImageButton redo = (ImageButton) findViewById(R.id.redo);
        redo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                richEditText.redo();
            }
        });

        ImageButton underline = (ImageButton) findViewById(R.id.underline);
        underline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                richEditText.underline(!richEditText.contains(RichEditText.FORMAT_UNDERLINED));
            }
        });


        ImageButton bullet = (ImageButton) findViewById(R.id.bullet);
        bullet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                richEditText.bullet(!richEditText.contains(RichEditText.FORMAT_BULLET));
            }
        });


        ImageButton bold = (ImageButton) findViewById(R.id.bold);
        bold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                richEditText.bold(!richEditText.contains(RichEditText.FORMAT_BOLD));
            }
        });

        ImageButton italic = (ImageButton) findViewById(R.id.italic);
        italic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                richEditText.italic(!richEditText.contains(RichEditText.FORMAT_ITALIC));
            }
        });

        //
        ImageButton quote = (ImageButton) findViewById(R.id.quote);
        quote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                richEditText.quote(!richEditText.contains(RichEditText.FORMAT_QUOTE));
            }
        });

        //插入图片处理事件(需要权限申请)
        ImageButton pic = (ImageButton) findViewById(R.id.pic);
        pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(EditActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.
                        PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(EditActivity.this, new
                            String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    openAlbum();
                }
            }
        });

        Button save_blog = (Button) findViewById(R.id.save);
        save_blog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //先处理空字符的异常情况并提示,否则则正常保存.
                if (title.getText().toString().trim().equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);
                    builder.setTitle("保存失败！");
                    builder.setMessage("标题不能为空！\n请重新编辑！");
                    builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    builder.show();
                } else {
                    //进行存储,保存日期/标题/正文/预览 并决定是插入还是更新
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    Date date = new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    String dateString = sdf.format(date);
                    values.put("date", dateString);
                    values.put("title", title.getText().toString());
                    values.put("passage", richEditText.toHtml());
                    values.put("preview", richEditText.getText().toString().replaceAll("\r|\n|\t", "  "));
                    String res = "更新成功！";
                    if (isnew) {
                        rowid = String.valueOf(db.insert("Blogs", null, values));
                    } else {
                        rowid = intent.getStringExtra("_id_");
                        db.update("Blogs", values, "id = ?", new String[]{rowid});
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);
                    builder.setTitle("提示");
                    builder.setMessage(res);
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(EditActivity.this,com.example.mol74.lx233_blogstest.MainActivity.class);
                            startActivity(intent);
                        }
                    });
                    builder.setNegativeButton("预览", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(EditActivity.this, CheckActivity.class);
                            intent.putExtra("_id_", rowid);
                            startActivity(intent);
                        }
                    });


                    builder.show();
                }
            }
        });
    }

    //启动相册进程
    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);
    }

    //动态获取访问相册权限
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.
                        PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(this, "You denied the permission",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    //打开相册图片并且获取了路径
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    richEditText.image(uri);
                }
                break;
            default:
                break;
        }
    }
}
