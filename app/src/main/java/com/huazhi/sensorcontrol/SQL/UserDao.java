package com.huazhi.sensorcontrol.SQL;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.huazhi.sensorcontrol.utils.LogUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserDao {
    private static SQLiteOpenHelper dbContect;
    private static UserDao userDao=null;
    private UserDao(Context context){dbContect=new DbContect(context);}
    public static UserDao getInstance(Context context){
        if(userDao==null){
            userDao=new UserDao(context);
        }
        return userDao;
    }
    public void savedb(String value, String type,String name) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate =  new Date(System.currentTimeMillis());
        //获取当前时间
        SQLiteDatabase db=dbContect.getWritableDatabase();
        String  time =  formatter.format(curDate);
        ContentValues values=new ContentValues();
        values.put("name",name);
        values.put("value",value);
        values.put("type",type);
        values.put("createtime",time);
        db.insert("tempdata1",null,values);//更改数据库要改顺序3
        //关闭数据库
        db.close();
    }
    public List<User> Show(String type) {
        SQLiteDatabase db = dbContect.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from tempdata1 where type= ? ", new String[]{type});
        List<User> userList = new ArrayList<>();
        while (cursor.moveToNext()) {
            User user = new User();
            user.setValue(cursor.getString(2));
            user.setType(cursor.getString(3));
            user.setCreatetime(cursor.getString(4));
            user.setName(cursor.getString(1));
            userList.add(user);
        }
        cursor.close();
        return userList;
    }
    public List<User> find1(String type) {
        //实例化数据库对象
        SQLiteDatabase db=dbContect.getWritableDatabase();
        //构造查询数据
        //更改数据库要改顺序5
        Cursor cursor=db.query("tempdata1",new String[]{"id","name","value","type","createtime"},
                "type==?",new String[]{type},null,null,null);
        List<User> userList = new ArrayList<>();
        while (cursor.moveToNext()){
            User user = new User();
            user.setId(cursor.getInt(0));
            user.setName(cursor.getString(1));
            user.setValue(cursor.getString(2));
            user.setType(cursor.getString(3));
            user.setCreatetime(cursor.getString(4));
            userList.add(user);
        }
        //关闭数据库
        db.close();
        return userList;
    }

}
