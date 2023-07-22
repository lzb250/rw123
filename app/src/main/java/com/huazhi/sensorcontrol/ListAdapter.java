package com.huazhi.sensorcontrol;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.huazhi.sensorcontrol.SQL.User;
import com.huazhi.sensorcontrol.utils.LogUtils;

import java.util.List;

public class ListAdapter extends BaseAdapter {

    private final Context context;
    private final List<User> userList;

    public ListAdapter(Context context, List<User> userList) {
        this.context=context;
        this.userList=userList;
    }

    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public Object getItem(int i) {
        return userList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return userList.get(i).getId();
    }
    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        if (convertView==null){
            convertView=View.inflate(context,R.layout.item_user,null);
        }
        //获取空间
        TextView tvvalue = convertView.findViewById(R.id.table_data);
        TextView tvtype = convertView.findViewById(R.id.table_type);
        TextView tvcreatetime = convertView.findViewById(R.id.table_date);
        TextView tvname=convertView.findViewById(R.id.table_name);
        //设置数据
        tvvalue.setText(userList.get(i).getValue());
        tvtype.setText(userList.get(i).getType());
        tvcreatetime.setText(userList.get(i).getCreatetime());
        tvname.setText(userList.get(i).getName());
        return convertView;
    }
}
