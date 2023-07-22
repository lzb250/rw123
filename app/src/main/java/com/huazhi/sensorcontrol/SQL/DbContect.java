package com.huazhi.sensorcontrol.SQL;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;
public class DbContect extends SQLiteOpenHelper {
    public DbContect(Context context){
        super(context,"tempdata1",null,1);

    }
    //创建数据库
    public void onCreate(SQLiteDatabase db){
        //创建密码表  pwd_tb
        db.execSQL("create table tempdata1(id integer primary key autoincrement," +
                "name text,value text,type text,createtime text)");

        }

    //数据库版本更新
    public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion)
    {
    }


}

