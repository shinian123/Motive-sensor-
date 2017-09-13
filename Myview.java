package com.example.chen.sesor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by chen on 2017/4/15.
 */

public class Myview extends View {
    public float r; 
    public double angle;
    public double direction_angle;
   public Myview(Context context,float r,float myangle,float directon){
       super(context);
       this.r=r;
       this.angle=3.141592653*myangle/180;
       this.direction_angle=3.141592653*directon/180;
   }
    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        Paint p = new Paint();
        p.setColor(Color.GREEN);// 设置绿色
        p.setStrokeWidth(2.0f);
        p.setTextSize(50.0f);
        final int bias=200;
        canvas.drawText("北",480-bias,280,p);
        canvas.drawText("南",480-bias,750,p);
        canvas.drawText("东",700-bias,500,p);
        canvas.drawText("西",250-bias,500,p);
        //p.setColor(Color.BLACK);
        //canvas.drawText("小星星",500,500,p);
        p.setColor(Color.BLUE);
        p.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(500-bias,500,200,p);
        p.setColor(Color.BLACK);
        canvas.drawLine(500-bias,500,500-bias+100*r*(float)Math.cos(angle),500-100*r*(float)Math.sin(angle),p);
        canvas.drawLine(500-bias+90*r*(float)Math.cos(angle+0.1),500-90*r*(float)Math.sin(angle+0.1),500-bias+100*r*(float)Math.cos(angle),500-100*r*(float)Math.sin(angle),p);
        canvas.drawLine(500-bias+90*r*(float)Math.cos(angle-0.1),500-90*r*(float)Math.sin(angle-0.1),500-bias+100*r*(float)Math.cos(angle),500-100*r*(float)Math.sin(angle),p);
        /*p.setColor(Color.MAGENTA);
        p.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(600+bias,500,150,p);
        p.setColor(Color.BLACK);
        canvas.drawLine(600+bias,500,600+bias,500-150,p);
        canvas.drawLine(600+bias+140*(float)Math.sin(0.1),500-140*(float)Math.cos(0.1),600+bias,500-150,p);
        canvas.drawLine(600+bias-140*(float)Math.sin(0.1),500-140*(float)Math.cos(-0.1),600+bias,500-150,p);
        p.setColor(Color.RED);
        canvas.drawLine(600+bias,500,600+bias+150*(float)Math.cos(direction_angle),500-150*(float)Math.sin(direction_angle),p);
        canvas.drawText("N",600+bias+150*(float)Math.cos(direction_angle),500-150*(float)Math.sin(direction_angle),p);*/
    }
}
