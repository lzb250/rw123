package com.huazhi.sensorcontrol;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.huazhi.sensorcontrol.SQL.User;
import com.huazhi.sensorcontrol.SQL.UserDao;
import com.huazhi.sensorcontrol.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class PictureActivity extends AppCompatActivity {

    //布局中变量的声明
    private LineChart lineChart_temp,lineChart_shi,lineChart_guang;
    private ImageButton ibBack;
    //保存数据
    List<Entry> list1 = new ArrayList<>();
    List<Entry> list2 = new ArrayList<>();
    List<Entry> list3 = new ArrayList<>();
    //时间声明
    private Timer timer;
    //变量声明
    int num_temp=0,num_shi=0,num_guang=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pic);
        //初始化视图
        lineChart_temp = findViewById(R.id.lc_temp);//温度折线图
        lineChart_shi = findViewById(R.id.lc_shi);//湿度折线图
        lineChart_guang = findViewById(R.id.lc_guang);//光照度折线图
        ibBack = findViewById(R.id.back);//返回上一界面
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //返回上一级，并且返回数据
                Intent intent=new Intent();
                Bundle bundle=new Bundle();
                bundle.putString("back_picture","OK");//图片界面成功返回
                intent.putExtras(bundle);
                setResult(Activity.RESULT_OK,intent);
                finish();
            }
        });
        //图像设置
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                UserDao userDao = UserDao.getInstance(getApplicationContext());
                List<User> userList1 = userDao.find1("厢内温度");
                if ((userList1.size()>num_temp)||(num_temp==0)){
                    list1.clear();
                    temp_picture();
                }
                List<User> userList2 = userDao.find1("厢外温度");
                if ((userList2.size()>num_shi)||(num_shi==0)){
                    list2.clear();
                    temp_picture_wai();
                }
                List<User> userList3 = userDao.find1("光照");
                if ((userList3.size()>num_guang)||(num_guang==0)){
                    list3.clear();
                    guang_picture();
                }
            }
        },0,5000);
    }
    //厢内温度图像
    private void temp_picture() {
        //添加数据
        UserDao userDao = UserDao.getInstance(getApplicationContext());
        List<User> userList1 = userDao.find1("厢内温度");
        System.out.println("条件查询数据组数：" + userList1.size());
        num_temp=userList1.size();
        int len_start,len_end=userList1.size();
        LogUtils.i("recvData=" + userList1.size());
        LogUtils.i("recvData=" + 7);
        if (userList1.size() > 7) {
            len_start = userList1.size() - 7;
            LogUtils.i("recvData=" + len_start);
            LogUtils.i("recvData=" + 1);
        } else {
            len_start = 0;
            LogUtils.i("recvData=" + 0);
        }
        for (int i = 0; i < 7; i++) {
            LogUtils.i("recvData=" + 2);
            User user = (User) userList1.get(len_start);
            double value = Double.parseDouble(user.getValue());
            list1.add(new Entry(i, (float) value));//第一个数字对应的是x轴，第二个对应的是y轴
            System.out.println("value:" + value);
            len_start++;
            len_end--;
            if (len_end<=0) break;
        }

        //初始化图表数据
        //list是这条线的数据 "温度"是这条线的描述
        LineDataSet lineDataSet = new LineDataSet(list1, "厢内温度");
        LineData lineData = new LineData(lineDataSet);
        lineChart_temp.setData(lineData);
        //折线图背景
        lineChart_temp.getXAxis().setDrawGridLines(false);  //是否绘制X轴上的网格线（背景里面的竖线）
        lineChart_temp.getAxisLeft().setDrawGridLines(false);  //是否绘制Y轴上的网格线（背景里面的横线）
        //对于右下角一串字母的操作
        lineChart_temp.getDescription().setEnabled(false);                  //是否显示右下角描述
        lineChart_temp.getDescription().setText("这是修改那串英文的方法");    //修改右下角字母的显示
        lineChart_temp.getDescription().setTextSize(20);                    //字体大小
        lineChart_temp.getDescription().setTextColor(Color.RED);             //字体颜色
        //图例
        Legend legend = lineChart_temp.getLegend();
        legend.setEnabled(true);//是否显示图例
        legend.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);//图例的位置

        //设置x轴
        XAxis xAxis = lineChart_temp.getXAxis();
        xAxis.setDrawGridLines(false); // 不绘制网格线
        xAxis.setAxisLineColor(Color.GRAY);//x轴颜色
        xAxis.setAxisLineWidth(2);//x轴粗细
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); //x轴所在位置，在底部显示
        xAxis.setAxisMaximum(6);   //X轴最大数值
        xAxis.setAxisMinimum(0);   //X轴最小数值
        //X轴坐标的个数  第二个参数一般填false  true表示强制设置标签数 可能会导致X轴坐标显示不全等问题
        xAxis.setLabelCount(6, false);

        //设置y轴
        YAxis yAxisLeft = lineChart_temp.getAxisLeft();// 左边Y轴
        yAxisLeft.setDrawGridLines(false);  //是否绘制Y轴上的网格线（背景里面的横线）
        yAxisLeft.setAxisLineColor(Color.BLACK);  //Y轴颜色
        yAxisLeft.setAxisLineWidth(2);           //Y轴粗细
        yAxisLeft.setValueFormatter(new IAxisValueFormatter() {  //Y轴自定义坐标
            @Override
            public String getFormattedValue(float v, AxisBase axisBase) {
                if (v == 0) {
                    return "0℃";
                }
                if (v == 10) {
                    return "10℃";
                }
                if (v == 20) {
                    return "20℃";
                }
                if (v == 30) {
                    return "30℃";
                }
                if (v == 40) {
                    return "40℃";
                }
                return "";
            }
        });
        yAxisLeft.setAxisMaximum(40);   //Y轴最大数值
        yAxisLeft.setAxisMinimum(0);   //Y轴最小数值
        //Y轴坐标的个数    第二个参数一般填false     true表示强制设置标签数 可能会导致X轴坐标显示不全等问题
        yAxisLeft.setLabelCount(40, false);
        //是否隐藏右边的Y轴（不设置的话有两条Y轴 同理可以隐藏左边的Y轴）
        lineChart_temp.getAxisRight().setEnabled(false);

        //设置折线的式样   这个是圆滑的曲线（有好几种自己试试） 默认是直线
        //lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        lineDataSet.setColor(Color.BLUE);  //折线的颜色
        lineDataSet.setLineWidth(2);        //折线的粗细
        //是否画折线点上的空心圆  false表示直接画成实心圆
        lineDataSet.setDrawCircleHole(false);
        lineDataSet.setCircleHoleRadius(3);  //空心圆的圆心半径
        //圆点的颜色     可以实现超过某个值定义成某个颜色的功能   这里先不讲 后面单独写一篇
        lineDataSet.setCircleColor(Color.RED);
        lineDataSet.setCircleRadius(3);      //圆点的半径
        //定义折线上的数据显示    可以实现加单位    以及显示整数（默认是显示小数）
        lineDataSet.setValueFormatter(new IValueFormatter() {
            public String getFormattedValue(float v, Entry entry, int i, ViewPortHandler viewPortHandler) {
                if (entry.getY() == v) {
                    return v + "℃";
                }
                return "";
            }
        });
        //数据更新
        lineChart_temp.notifyDataSetChanged();
        lineChart_temp.invalidate();
    }
    //厢外温度图像
    private void temp_picture_wai() {
        //添加数据
        UserDao userDao = UserDao.getInstance(getApplicationContext());
        List<User> userList1 = userDao.find1("厢外温度");
        System.out.println("条件查询数据组数：" + userList1.size());
        num_temp=userList1.size();
        int len_start,len_end=userList1.size();
        if (userList1.size() > 7) {
            len_start = userList1.size() - 7;
        } else {
            len_start = 0;
        }
        for (int i = 0; i < 7; i++) {
            User user = (User) userList1.get(len_start);
            double value = Double.parseDouble(user.getValue());
            list2.add(new Entry(i, (float) value));//第一个数字对应的是x轴，第二个对应的是y轴
            System.out.println("value:" + value);
            len_start++;
            len_end--;
            if (len_end<=0) break;
        }

        //初始化图表数据
        //list是这条线的数据 "温度"是这条线的描述
        LineDataSet lineDataSet = new LineDataSet(list2, "厢外温度");
        LineData lineData = new LineData(lineDataSet);
        lineChart_temp.setData(lineData);
        //折线图背景
        lineChart_temp.getXAxis().setDrawGridLines(false);  //是否绘制X轴上的网格线（背景里面的竖线）
        lineChart_temp.getAxisLeft().setDrawGridLines(false);  //是否绘制Y轴上的网格线（背景里面的横线）
        //对于右下角一串字母的操作
        lineChart_temp.getDescription().setEnabled(false);                  //是否显示右下角描述
        lineChart_temp.getDescription().setText("这是修改那串英文的方法");    //修改右下角字母的显示
        lineChart_temp.getDescription().setTextSize(20);                    //字体大小
        lineChart_temp.getDescription().setTextColor(Color.RED);             //字体颜色
        //图例
        Legend legend = lineChart_temp.getLegend();
        legend.setEnabled(true);//是否显示图例
        legend.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);//图例的位置

        //设置x轴
        XAxis xAxis = lineChart_temp.getXAxis();
        xAxis.setDrawGridLines(false); // 不绘制网格线
        xAxis.setAxisLineColor(Color.GRAY);//x轴颜色
        xAxis.setAxisLineWidth(2);//x轴粗细
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); //x轴所在位置，在底部显示
        xAxis.setAxisMaximum(6);   //X轴最大数值
        xAxis.setAxisMinimum(0);   //X轴最小数值
        //X轴坐标的个数  第二个参数一般填false  true表示强制设置标签数 可能会导致X轴坐标显示不全等问题
        xAxis.setLabelCount(6, false);

        //设置y轴
        YAxis yAxisLeft = lineChart_temp.getAxisLeft();// 左边Y轴
        yAxisLeft.setDrawGridLines(false);  //是否绘制Y轴上的网格线（背景里面的横线）
        yAxisLeft.setAxisLineColor(Color.BLACK);  //Y轴颜色
        yAxisLeft.setAxisLineWidth(2);           //Y轴粗细
        yAxisLeft.setValueFormatter(new IAxisValueFormatter() {  //Y轴自定义坐标
            @Override
            public String getFormattedValue(float v, AxisBase axisBase) {
                if (v == 0) {
                    return "0℃";
                }
                if (v == 10) {
                    return "10℃";
                }
                if (v == 20) {
                    return "20℃";
                }
                if (v == 30) {
                    return "30℃";
                }
                if (v == 40) {
                    return "40℃";
                }
                return "";
            }
        });
        yAxisLeft.setAxisMaximum(40);   //Y轴最大数值
        yAxisLeft.setAxisMinimum(0);   //Y轴最小数值
        //Y轴坐标的个数    第二个参数一般填false     true表示强制设置标签数 可能会导致X轴坐标显示不全等问题
        yAxisLeft.setLabelCount(40, false);
        //是否隐藏右边的Y轴（不设置的话有两条Y轴 同理可以隐藏左边的Y轴）
        lineChart_temp.getAxisRight().setEnabled(false);

        //设置折线的式样   这个是圆滑的曲线（有好几种自己试试） 默认是直线
        //lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        lineDataSet.setColor(Color.RED);  //折线的颜色
        lineDataSet.setLineWidth(2);        //折线的粗细
        //是否画折线点上的空心圆  false表示直接画成实心圆
        lineDataSet.setDrawCircleHole(false);
        lineDataSet.setCircleHoleRadius(3);  //空心圆的圆心半径
        //圆点的颜色     可以实现超过某个值定义成某个颜色的功能   这里先不讲 后面单独写一篇
        lineDataSet.setCircleColor(Color.RED);
        lineDataSet.setCircleRadius(3);      //圆点的半径
        //定义折线上的数据显示    可以实现加单位    以及显示整数（默认是显示小数）
        lineDataSet.setValueFormatter(new IValueFormatter() {
            public String getFormattedValue(float v, Entry entry, int i, ViewPortHandler viewPortHandler) {
                if (entry.getY() == v) {
                    return v + "℃";
                }
                return "";
            }
        });
        //数据更新
        lineChart_temp.notifyDataSetChanged();
        lineChart_temp.invalidate();
    }
    //湿度图像
    /*private void shi_picture() {
        //添加数据
        UserDao userDao = UserDao.getInstance(getApplicationContext());
        List<User> userList2 = userDao.find1("湿度");
        System.out.println("条件查询数据组数：" + userList2.size());
        num_shi=userList2.size();
        int len_start,len_end=userList2.size();
        if (userList2.size() > 7) {
            len_start = userList2.size() - 7;
        } else {
            len_start = 0;
        }
        for (int i = 0; i < 7; i++) {
            User user = (User) userList2.get(len_start);
            double value = Double.parseDouble(user.getValue());
            list2.add(new Entry(i, (float) value));//第一个数字对应的是x轴，第二个对应的是y轴
            System.out.println("value:" + value);
            len_start++;
            len_end--;
            if (len_end<=0) break;
        }

        //初始化图表数据
        //list是这条线的数据 "温度"是这条线的描述
        LineDataSet lineDataSet = new LineDataSet(list2, "湿度");
        LineData lineData = new LineData(lineDataSet);
        lineChart_shi.setData(lineData);
        //折线图背景
        lineChart_shi.getXAxis().setDrawGridLines(false);  //是否绘制X轴上的网格线（背景里面的竖线）
        lineChart_shi.getAxisLeft().setDrawGridLines(false);  //是否绘制Y轴上的网格线（背景里面的横线）
        //对于右下角一串字母的操作
        lineChart_shi.getDescription().setEnabled(false);                  //是否显示右下角描述
        lineChart_shi.getDescription().setText("这是修改那串英文的方法");    //修改右下角字母的显示
        lineChart_shi.getDescription().setTextSize(20);                    //字体大小
        lineChart_shi.getDescription().setTextColor(Color.RED);             //字体颜色
        //图例
        Legend legend = lineChart_shi.getLegend();
        legend.setEnabled(true);//是否显示图例
        legend.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);//图例的位置

        //设置x轴
        XAxis xAxis = lineChart_shi.getXAxis();
        xAxis.setDrawGridLines(false); // 不绘制网格线
        xAxis.setAxisLineColor(Color.GRAY);//x轴颜色
        xAxis.setAxisLineWidth(2);//x轴粗细
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); //x轴所在位置，在底部显示
        xAxis.setAxisMaximum(6);   //X轴最大数值
        xAxis.setAxisMinimum(0);   //X轴最小数值
        //X轴坐标的个数  第二个参数一般填false  true表示强制设置标签数 可能会导致X轴坐标显示不全等问题
        xAxis.setLabelCount(6, false);

        //设置y轴
        YAxis yAxisLeft = lineChart_shi.getAxisLeft();// 左边Y轴
        yAxisLeft.setDrawGridLines(false);  //是否绘制Y轴上的网格线（背景里面的横线）
        yAxisLeft.setAxisLineColor(Color.BLACK);  //Y轴颜色
        yAxisLeft.setAxisLineWidth(2);           //Y轴粗细
        yAxisLeft.setValueFormatter(new IAxisValueFormatter() {  //Y轴自定义坐标
            @Override
            public String getFormattedValue(float v, AxisBase axisBase) {
                if (v == 0) {
                    return "0%";
                }
                if (v == 20) {
                    return "20%";
                }
                if (v == 40) {
                    return "40%";
                }
                if (v == 60) {
                    return "60%";
                }
                if (v == 80) {
                    return "80%";
                }
                if (v == 100) {
                    return "100%";
                }
                return "";
            }
        });
        yAxisLeft.setAxisMaximum(100);   //Y轴最大数值
        yAxisLeft.setAxisMinimum(0);   //Y轴最小数值
        //Y轴坐标的个数    第二个参数一般填false     true表示强制设置标签数 可能会导致X轴坐标显示不全等问题
        yAxisLeft.setLabelCount(100, false);
        //是否隐藏右边的Y轴（不设置的话有两条Y轴 同理可以隐藏左边的Y轴）
        lineChart_shi.getAxisRight().setEnabled(false);

        //设置折线的式样   这个是圆滑的曲线（有好几种自己试试） 默认是直线
        //lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        lineDataSet.setColor(Color.GREEN);  //折线的颜色
        lineDataSet.setLineWidth(2);        //折线的粗细
        //是否画折线点上的空心圆  false表示直接画成实心圆
        lineDataSet.setDrawCircleHole(false);
        lineDataSet.setCircleHoleRadius(3);  //空心圆的圆心半径
        //圆点的颜色     可以实现超过某个值定义成某个颜色的功能   这里先不讲 后面单独写一篇
        lineDataSet.setCircleColor(Color.RED);
        lineDataSet.setCircleRadius(3);      //圆点的半径
        //定义折线上的数据显示    可以实现加单位    以及显示整数（默认是显示小数）
        lineDataSet.setValueFormatter(new IValueFormatter() {
            public String getFormattedValue(float v, Entry entry, int i, ViewPortHandler viewPortHandler) {
                if (entry.getY() == v) {
                    return v + "%";
                }
                return "";
            }
        });
        //数据更新
        lineChart_shi.notifyDataSetChanged();
        lineChart_shi.invalidate();
    }*/
    //光照度图像
    private void guang_picture() {
        //添加数据
        UserDao userDao = UserDao.getInstance(getApplicationContext());
        List<User> userList3 = userDao.find1("光照");
        System.out.println("条件查询数据组数：" + userList3.size());
        num_guang=userList3.size();
        int len_start,len_end=userList3.size();
        if (userList3.size() > 7) {
            len_start = userList3.size() - 7;
        } else {
            len_start = 0;
        }
        for (int i = 0; i < 7; i++) {
            User user = (User) userList3.get(len_start);
            double value = Double.parseDouble(user.getValue());
            list3.add(new Entry(i, (float) value));//第一个数字对应的是x轴，第二个对应的是y轴
            System.out.println("value:" + value);
            len_start++;
            len_end--;
            if (len_end<=0) break;
        }

        //初始化图表数据
        //list是这条线的数据 "光照度"是这条线的描述
        LineDataSet lineDataSet = new LineDataSet(list3, "光照度");
        LineData lineData = new LineData(lineDataSet);
        lineChart_guang.setData(lineData);
        //折线图背景
        lineChart_guang.getXAxis().setDrawGridLines(false);  //是否绘制X轴上的网格线（背景里面的竖线）
        lineChart_guang.getAxisLeft().setDrawGridLines(false);  //是否绘制Y轴上的网格线（背景里面的横线）
        //对于右下角一串字母的操作
        lineChart_guang.getDescription().setEnabled(false);                  //是否显示右下角描述
        lineChart_guang.getDescription().setText("这是修改那串英文的方法");    //修改右下角字母的显示
        lineChart_guang.getDescription().setTextSize(20);                    //字体大小
        lineChart_guang.getDescription().setTextColor(Color.RED);             //字体颜色
        //图例
        Legend legend = lineChart_guang.getLegend();
        legend.setEnabled(true);//是否显示图例
        legend.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);//图例的位置

        //设置x轴
        XAxis xAxis = lineChart_guang.getXAxis();
        xAxis.setDrawGridLines(false); // 不绘制网格线
        xAxis.setAxisLineColor(Color.GRAY);//x轴颜色
        xAxis.setAxisLineWidth(2);//x轴粗细
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); //x轴所在位置，在底部显示
        xAxis.setAxisMaximum(6);   //X轴最大数值
        xAxis.setAxisMinimum(0);   //X轴最小数值
        //X轴坐标的个数  第二个参数一般填false  true表示强制设置标签数 可能会导致X轴坐标显示不全等问题
        xAxis.setLabelCount(6, false);

        //设置y轴
        YAxis yAxisLeft = lineChart_guang.getAxisLeft();// 左边Y轴
        yAxisLeft.setDrawGridLines(false);  //是否绘制Y轴上的网格线（背景里面的横线）
        yAxisLeft.setAxisLineColor(Color.BLACK);  //Y轴颜色
        yAxisLeft.setAxisLineWidth(2);           //Y轴粗细
        yAxisLeft.setValueFormatter(new IAxisValueFormatter() {  //Y轴自定义坐标
            @Override
            public String getFormattedValue(float v, AxisBase axisBase) {
                if (v == 0) {
                    return "0";
                }
                if (v == 200) {
                    return "200";
                }
                if (v == 400) {
                    return "400";
                }
                if (v == 600) {
                    return "600";
                }
                if (v == 800) {
                    return "800";
                }
                if (v == 1000) {
                    return "1000";
                }
                return "";
            }
        });
        yAxisLeft.setAxisMaximum(1000);   //Y轴最大数值
        yAxisLeft.setAxisMinimum(0);   //Y轴最小数值
        //Y轴坐标的个数    第二个参数一般填false     true表示强制设置标签数 可能会导致X轴坐标显示不全等问题
        yAxisLeft.setLabelCount(1000, false);
        //是否隐藏右边的Y轴（不设置的话有两条Y轴 同理可以隐藏左边的Y轴）
        lineChart_guang.getAxisRight().setEnabled(false);

        //设置折线的式样   这个是圆滑的曲线（有好几种自己试试） 默认是直线
        //lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        lineDataSet.setColor(Color.YELLOW);  //折线的颜色
        lineDataSet.setLineWidth(2);        //折线的粗细
        //是否画折线点上的空心圆  false表示直接画成实心圆
        lineDataSet.setDrawCircleHole(false);
        lineDataSet.setCircleHoleRadius(3);  //空心圆的圆心半径
        //圆点的颜色     可以实现超过某个值定义成某个颜色的功能   这里先不讲 后面单独写一篇
        lineDataSet.setCircleColor(Color.RED);
        lineDataSet.setCircleRadius(3);      //圆点的半径
        //定义折线上的数据显示    可以实现加单位    以及显示整数（默认是显示小数）
        lineDataSet.setValueFormatter(new IValueFormatter() {
            public String getFormattedValue(float v, Entry entry, int i, ViewPortHandler viewPortHandler) {
                if (entry.getY() == v) {
                    return v+"";
                }
                return "";
            }
        });
        //数据更新
        lineChart_guang.notifyDataSetChanged();
        lineChart_guang.invalidate();
    }
}