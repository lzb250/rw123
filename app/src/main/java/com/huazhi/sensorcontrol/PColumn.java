package com.huazhi.sensorcontrol;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.List;

public class PColumn extends View {
    private static Context context = null;
    private static AttributeSet attributeSet = null;

    //自定义view实现柱状图
    //首先定义一个类实现View
    //定义画笔
    private Paint mLinePaint;
    private Paint mGreenPaint;
    private Paint mTextPaint;
    //定义上下文
    private Context mContext;
    //定义宽高
    private float weight;
    private float height;
    private float mScale;
    //这个数组是高度的值(y轴上的数值)
    private static String[] y_title = {"", "", "", "" , "", "", ""};
    //分别为定义数据与数据源名称的集合
    private static List<Double> mData;
    private static List<Integer> mNames;
    private static int max;

    public PColumn(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        PColumn.context = context;
        attributeSet = attrs;
        //给定义的画笔进行加工
        mContext = context;
        mLinePaint = new Paint();
        mGreenPaint = new Paint();
        mTextPaint = new Paint();

        mLinePaint.setARGB(255, 0, 0, 0);
        mGreenPaint.setARGB(255, 0, 200, 149);
        mTextPaint.setARGB(255, 0, 0, 0);

        mGreenPaint.setStyle(Paint.Style.FILL);

        mTextPaint.setAntiAlias(true);
        mGreenPaint.setAntiAlias(true);
        mLinePaint.setAntiAlias(true);

        mScale = context.getResources().getDisplayMetrics().density;
    }

    //尺寸发生改变的时候调用
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        weight = 0.7F * w;
        height = 0.70F * h;
    }
    //绘制
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //把整个区域分为5份，画了6条线加以区分
        float min_height = height / 5;
        for (int i = 5; i >= 0; i--) {//总共有6根线
            if (i == 5) {//最下面那根线颜色比较深
                mLinePaint.setARGB(255, 0, 0, 0);
            } else {//其余线的颜色
                mLinePaint.setARGB(255, 112, 128, 144);
            }
            canvas.drawLine(70 * mScale, 30 * mScale + min_height * i, 70 * mScale + weight, 30 * mScale + min_height * i, mLinePaint);
            //线对应的数值写上去
            mTextPaint.setTextAlign(Paint.Align.RIGHT);
            mTextPaint.setTextSize(10 * mScale);
            canvas.drawText(y_title[i], 60 * mScale, 32 * mScale + min_height * i, mTextPaint);
        }
        //根据需要画的数据将X轴分为几部分
        float min_weight = (weight - 70 * mScale) / (mData.size());
        //每一组数据对应的id号写在X轴下面
        mTextPaint.setTextSize(12 * mScale);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        //绘制每组数据的柱形图
        for (int i = 0; i < mData.size(); i++) {
            int leftR = (int) (70 * mScale + i * min_weight + min_weight / 2);
            int rightR = leftR + (int) (min_weight / 2);
            int buttomR = (int) (30 * mScale + min_height * 5);
            int topR = buttomR - (int) (height / max * mData.get(i));
            canvas.drawRect(new RectF(leftR, topR, rightR, buttomR), mGreenPaint);
            mTextPaint.setARGB(255, 0, 0, 0);
            canvas.drawText(String.valueOf(mNames.get(i)), leftR + min_weight / 4, buttomR + 20 * mScale, mTextPaint);
            mTextPaint.setARGB(255, 0, 0, 0);
            canvas.drawText(mData.get(i) + "", leftR + min_weight / 4, topR - 10 * mScale, mTextPaint);
        }
    }
    //传入数据并进行绘制
    public PColumn(List<Double> data, List<Integer> name) {
        super(PColumn.context,PColumn.attributeSet);
        mData = data;
        mNames = name;
        invalidate();
    }
    //传入Y轴数据
    public PColumn(String type_){
        super(PColumn.context,PColumn.attributeSet);
        if(type_.equals("guang")){
            y_title = new String[]{"500LUX", "400LUX", "300LUX", "200LUX", "100LUX","0LUX"};
            max=500;
        }else if (type_.equals("wen")){
            max=50;
            y_title = new String[]{"50℃","40℃","30℃","20℃","10℃","0℃"};
        }else if (type_.equals("shi")){
            max=100;
            y_title = new String[]{"100%","80%","60%","40%","20%","0%"};
        }
    }
}
