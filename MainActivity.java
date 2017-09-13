package com.example.chen.sesor;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class MainActivity extends Activity implements SensorEventListener {
    private SensorManager sensorManager = null;//传感器管理器
    private Sensor sensor = null, gravitySensor = null, linearAcceleSensor = null;
    private Sensor  compassSensor = null, orientSensor = null,rotVecSensor = null,stepSensor=null;
    private TextView liner_text = null;
    private TextView linear_text = null;
    private Button button_restart=null;
    private Button button_sure=null;
    private AutoCompleteTextView height_edit=null;
    private TextView steplength_text = null;
    private TextView normal_text = null;
    private TextView gravity_text = null;
    private TextView process_text = null;
    private Button button_caliborate =null,button_post=null;
    private WindowManager window = null;
    private Sensor accelSensor = null;
    private boolean isAllowCaliborate=false;
    private boolean isPost=false;
    private float[] rotVecValues = null;
    private LinearLayout my_Canvas=null;
    private final float accelerate_walking_limit =1.5f;//加速度阈值
    private final int Queue_Length=32;//采样点数
    private float direction_angle;
    private Motion motion;
    private RequestQueue mQueue;
    private String string_ip="";
    private static int times=0;
    private static int data_count=0;
    Walker walker;
    ThreeNums laverageAcc,averageAcc,compass,gravity,lineraccele,accele,orient;//前两个为线性加速度和合加速度的平均值
    ThreeNums result, minuslinear, minusgravity, gravity_absolute, compass_absolute;//地球坐标系下的各传感器数值
    //private View.OnClickListener mListener =null;
    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MyData myData=new MyData();
        myData.create_file("/mydata/data.txt");
        myData.append("data.txt","I love electronics!");
        walker=new Walker(0,0,0,0);//用户的步数、步长、身高等信息
        motion=new Motion(new ThreeNums(0,0,0),new ThreeNums(0,0,0),new ThreeNums(0,0,0));
        //记录加速度、速度、距离信息
        my_Canvas=(LinearLayout)findViewById(R.id.my_canvas);
        liner_text = (TextView) findViewById(R.id.current_step_count);
        linear_text = (TextView) findViewById(R.id.display_linear);
        normal_text = (TextView) findViewById(R.id.display_accelerator);
        gravity_text = (TextView) findViewById(R.id.display_gravity);
        process_text=(TextView) findViewById(R.id.display_process);
        steplength_text=(TextView) findViewById(R.id.steplength_text);
        height_edit =(AutoCompleteTextView)findViewById(R.id.height_editer);
        String[] array={"192.168.1.191","192.168.191.1","192.168.191.2"};
        ArrayAdapter arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,array);
        height_edit.setAdapter(arrayAdapter);
        button_restart=(Button)findViewById(R.id.button_restart);//重新开始
        button_sure =(Button)findViewById(R.id.sure);//确定按钮
        button_caliborate =(Button)findViewById(R.id.button_justify);//校准按钮
        button_post=(Button)findViewById(R.id.post);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);//加速度传感器
        gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);//重力传感器
        linearAcceleSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);//线性加速度传感器
        stepSensor =sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);//步数传感器
        accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        compassSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);//磁场传感器
        orientSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);//方向传感器

        rotVecSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);//旋转向量传感器
        button_caliborate.setOnClickListener(mListener);//按钮监听
        button_post.setOnTouchListener(tListener);
        //showInfo1("resolution is " + sensor.getResolution());
        //showInfo1("API为" + Build.VERSION.SDK_INT);
        window = (WindowManager) getSystemService(WINDOW_SERVICE);
        mQueue = Volley.newRequestQueue(getApplicationContext());
    }
    View.OnClickListener mListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {//按钮监听函数
            switch (v.getId()){
                case R.id.button_justify: caliborate();break;
                //case R.id.post:ispost();break;
                default:break;
            }
        }
    };
    View.OnTouchListener tListener =new View.OnTouchListener(){
        @Override
        public boolean onTouch(View v, MotionEvent event){
            if(v.getId()==R.id.post){
                if(event.getAction()==MotionEvent.ACTION_UP){
                    ispost();
                }
            }
            return false;
        }
    };

    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    @Override
    protected void onResume() {//注册传感器
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, gravitySensor, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, linearAcceleSensor, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, accelSensor, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, compassSensor,SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, orientSensor,SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, rotVecSensor,SensorManager.SENSOR_DELAY_GAME);
        super.onResume();
    }


    @Override
    protected void onPause() {//注销传感器
        sensorManager.unregisterListener(this, sensor);
        sensorManager.unregisterListener(this, gravitySensor);
        sensorManager.unregisterListener(this, linearAcceleSensor);
        sensorManager.unregisterListener(this, accelSensor);
        sensorManager.unregisterListener(this, compassSensor);
        sensorManager.unregisterListener(this, orientSensor);
        sensorManager.unregisterListener(this, rotVecSensor);
        sensorManager.unregisterListener(this, stepSensor);
        super.onPause();
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub
        //showInfo1(sensor.getName() + " accuracy changed: " + accuracy);
    }
    private boolean ready_compass=false,ready_gravity=false,ready_linear = false,ready_acce=false,
            ready_step=false,ready_calculate=false;//读传感器成功标志
    private int count;
    private List<ThreeNums> ac =new ArrayList<ThreeNums>();//加速度传感器平均值
    private List<ThreeNums> gr =new ArrayList<ThreeNums>();//重力传感器平均值
    private List<ThreeNums> lac =new ArrayList<ThreeNums>();//线性加速度传感器平均值
    private List <Integer>walk_count =new ArrayList<Integer>();//步数数组
    private List <Long>current_time =new ArrayList<Long>();//当前时间数组
    private List<ThreeNums> mobile_linear_accelerate = new ArrayList<>();//手机坐标系下的线性加速度
    private List<ThreeNums> earth_linear_accelerate = new ArrayList<>();//地球坐标系下的线性加速度
    @SuppressWarnings("deprecation")
    @Override
    public void onSensorChanged(SensorEvent event) {
        step_count_cal(event);
        SharedPreferences sp=getSharedPreferences("av_acc",Context.MODE_PRIVATE);
        if(sp.contains("x")){
            message_process(event);//处理传感器信息函数
        }else {
            if (ac.size() < 1000) {
                do_caliborate(event);//校准函数
            } else {
                message_average();//处理传感器信息函数
            }
        }
    }
    private void caliborate(){//校准按钮的响应函数
        process_text.setText("正在校准");
        ac.clear();
        lac.clear();
        gr.clear();
    }




    private float get_direction(SensorManager manager){
       /* double direction;
        ThreeNums three=ThreeNums.chaji(one,two);
        ThreeNums zuobiao=new ThreeNums(1,0,0);
        ThreeNums fenliang=ThreeNums.chaji(zuobiao,two);
        ThreeNums shiji=ThreeNums.chaji(fenliang,three);
        direction = ThreeNums.dianji(fenliang, three) / ThreeNums.mo(fenliang) / ThreeNums.mo(three);
        direction = Math.acos(direction) * 180 / 3.141592653;
        if(ThreeNums.dianji(two,shiji)<0) {
            direction = 360 - direction;
        }
        return (float)direction;*/
        float[] mR = new float[16];
        float[] mI = new float[16];
        float[] mOrientation = new float[3];
        float[] acele={accele.A,accele.B,accele.C};
        float[] magnet={compass.A,compass.B,compass.C};
        manager.getRotationMatrix(mR, mI,acele,magnet);
        manager.getOrientation(mR,mOrientation);
        //手机绕z轴旋转的度数
        float azimuth = (float) Math.toDegrees(mOrientation[0]);
        return azimuth;
    }
    private float get_angle(ThreeNums ONE,ThreeNums TWO,ThreeNums IN){
        double angle;
        ThreeNums THREE =ThreeNums.chaji(ONE, TWO);
        angle=180/3.1415926*Math.acos(ThreeNums.dianji(THREE,IN)/ThreeNums.mo(THREE)/ThreeNums.mo(IN));
        return (float) angle;
    }
    public void restart(View view){//重新开始按钮的相应函数
        walker.step_count=0;//步数清零
        showinfo(walker.step_count);
    }
    public void sure(View view){//确认按钮的响应函数
        String a="http://",b=height_edit.getText().toString(),c=":8000/disk/";
        string_ip=a+b+c;
        /*int height=Integer.parseInt(height_edit.getText().toString());
        walker.step_length=get_step_length(height);
        String msg=String.format("您的身高:%d厘米\t您的步长:%7.1f厘米",height,walker.step_length);*/
        steplength_text.setText(string_ip);
    }
    public float get_step_length(int height){//由身高获得步长的函数
        /*
     H=0.262S+155.911?
     or
     s=0.45*h?*/
        double step_length;
        step_length=height*0.45;
        return (float) step_length;
    }
    public void showinfo(int count){//显示当前步数和所走路程
        float distance = count*walker.step_length/100;
        String msg=String.format("当前步数：%d\n所走路程:%7.2f米",count,distance);
        liner_text.setText(msg);
    }
    private void step_count_cal(SensorEvent event){//计算当前步数
        int type = event.sensor.getType();
        if (type == Sensor.TYPE_STEP_DETECTOR) {
            if(event.values[0]==1.0f){
                walker.step_count++;
                ready_step=true;
                step_message_synthesis();
            }
        }
    }
    private void do_caliborate(SensorEvent event){//校准函数
        int type = event.sensor.getType();
        if (type == Sensor.TYPE_ACCELEROMETER) {
            ac.add(new ThreeNums(event.values[0],event.values[1],event.values[2]));
        } else if (type == Sensor.TYPE_GRAVITY) {
            gr.add(new ThreeNums(event.values[0],event.values[1],event.values[2]));
        } else if (type == Sensor.TYPE_LINEAR_ACCELERATION) {
            lac.add(new ThreeNums(event.values[0],event.values[1],event.values[2]));
        }
    }
    private void message_average() {//生成校准数据并保存在sharedpreferences
        averageAcc = ThreeNums.average(ac);//取平均
        //ac.clear();
        laverageAcc = ThreeNums.average(lac);
        //lac.clear();
        SharedPreferences sp = getSharedPreferences("av_acc", Context.MODE_PRIVATE);
        sp.edit().putFloat("x", averageAcc.A).putFloat("y", averageAcc.B).putFloat("z", averageAcc.C).commit();
        sp = getSharedPreferences("av_lacc", Context.MODE_PRIVATE);
        sp.edit().putFloat("x", laverageAcc.A).putFloat("y", laverageAcc.B).putFloat("z", laverageAcc.C).commit();
        ThreeNums averageGr = ThreeNums.average(gr);
        //gr.clear();
        String msg = String.format("校准完成\n校准后的平均线性加速度为\n：x:%7.3f\ty:%7.3f\tz:%7.3f\n" +
                        "校准后的平均合加速度为\n" +
                        "：x:%7.3f\ty:%7.3f\tz:%7.3f", laverageAcc.A, laverageAcc.B, laverageAcc.C,
                averageAcc.A, averageAcc.B, averageAcc.C);
        process_text.setText(msg);
    }
    private void message_process(SensorEvent event) {
        get_sensor_message(event);
        if(!(ready_compass&&ready_linear&&ready_gravity&&ready_acce))
            return;
        if(count++ % 1 == 0){
            accelerate_first_process();//加速度信息预处理
            if(!(ready_compass&&ready_linear&&ready_gravity&&ready_acce))
                return;
            accelerate_message_display();//加速度信息显示
            count = 1;
        }
    }
    private boolean is_motion_changed(ThreeNums a){//二维加速度是否超过阈值，超过返回true，否则返回false
        accelerate_first_process();
       if (ThreeNums.abs_2d(a) > accelerate_walking_limit)
                return true;
       else
                return false;

    }
    private void get_sensor_message(SensorEvent event){//获取传感器信息的函数
        switch(event.sensor.getType()){
            case Sensor.TYPE_GRAVITY:
                gravity=ThreeNums.form(event.values[0],event.values[1],event.values[2]);
                if(gravity.C!= 0)
                    ready_gravity = true;
                break;
            case Sensor.TYPE_ACCELEROMETER:
                accele= ThreeNums.form(event.values[0],event.values[1],event.values[2]);
                if(accele.C!= 0)
                    ready_acce = true;
                break;

            case Sensor.TYPE_MAGNETIC_FIELD:
                compass=ThreeNums.form(event.values[0],event.values[1],event.values[2]);
                if(compass.C != 0)
                    ready_compass = true;
                break;

            case Sensor.TYPE_ORIENTATION:
                orient=ThreeNums.form(event.values[0],event.values[1],event.values[2]);
                break;

            case Sensor.TYPE_ROTATION_VECTOR:
                if(rotVecValues == null){
                    rotVecValues = new float[event.values.length];
                }
                for(int i = 0; i < rotVecValues.length; i++){
                    rotVecValues[i] = event.values[i];
                }
                break;

            case Sensor.TYPE_LINEAR_ACCELERATION:
                lineraccele=ThreeNums.form(event.values[0],event.values[1],event.values[2]);
                if(lineraccele.C != 0)
                    ready_linear = true;
                break;

        }
    }

    private void accelerate_first_process(){//加速度信息预处理函数
        if (!(ready_compass && ready_linear && ready_gravity && ready_acce))
            return;
        SharedPreferences sp = getSharedPreferences("av_lacc", Context.MODE_PRIVATE);
        float x=sp.getFloat("x",0);
        float y=sp.getFloat("y",0);
        float z=sp.getFloat("z",0);
        ThreeNums my_laverageAcc=new ThreeNums(x,y,z);
         sp = getSharedPreferences("av_acc", Context.MODE_PRIVATE);
         x=sp.getFloat("x",0);
         y=sp.getFloat("y",0);
         z=sp.getFloat("z",0);
        ThreeNums my_averageAcc=new ThreeNums(x,y,z);
        minuslinear = new ThreeNums(lineraccele.A - my_laverageAcc.A, lineraccele.B - my_laverageAcc.B, lineraccele.C - my_laverageAcc.C * accele.C / my_averageAcc.C);
        minusgravity = new ThreeNums(gravity.A, gravity.B, gravity.C - (my_averageAcc.C - accele.C) / my_averageAcc.C * my_laverageAcc.C);
        result = getresult(accele,sensorManager);
        motion = Motion.getmotion(minuslinear, motion, 100 * 0.002f);
        ThreeNums_addqueue(accele, mobile_linear_accelerate, Queue_Length);
        ThreeNums_addqueue(result, earth_linear_accelerate, Queue_Length);

        if(result.C!=0)ready_calculate=true;
    }
    private void accelerate_message_display(){//加速度信息显示函数
        if(!ready_calculate)
            return;
        accclerate_analyse();//加速度信息分析函数
        Date dt=new Date();//Date对象以获取当前时间
        if(current_time.size()>0) {
            long time_evel = dt.getTime() - current_time.get(current_time.size() - 1);
            if (time_evel > 10000) {
                //liner_text.setText("你在原地");
            }
        }
       String msg=String.format("在手机坐标系下：\n重力加速度:" +
                        "x:%7.3f\ty%7.3f\tz:%7.3f\n磁场:x:%7.3f\ty%7.3f\tz:%7.3f\n" +
                        "合加速度:x:%7.3f\ty%7.3f\tz:%7.3f\n"+
                        "校准前的线性加速度:x:%7.3f\ty%7.3f\tz:%7.3f\n" +
                        "校准后的线性加速度:x:%7.3f\ty%7.3f\tz:%7.3f\n校准后的重力加速度:x:%7.3f\ty%7.3f\tz:%7.3f\n",
                gravity.A,gravity.B,gravity.C
                ,compass.A,compass.B,compass.C,accele.A,accele.B,accele.C,
                lineraccele.A,lineraccele.B,lineraccele.C,
                minuslinear.A,minuslinear.B,minuslinear.C
                ,minusgravity.A,minusgravity.B,minusgravity.C);
       msg+=String.format("在地球坐标系下\n校准后的线性加速度：x:%7.3f\ty%7.3f\tz:%7.3f\n",
                result.A,result.B,result.C);
        /*float angel_between;//手机和水平面夹角
        angel_between=(float)( Math.acos(gravity.C/ThreeNums.mo(gravity))*180/3.1415926);

        String msg1=String.format("手机与地面夹角：%7.3f,测试样本数：%d\n",angel_between,mobile_linear_accelerate.size());
        String msg=String.format("在手机坐标系下：\n校准后的线性加速度:x:%7.3f\ty%7.3f\tz:%7.3f\n" +
                "在地球坐标系下:\n校准后的线性加速度:x:%7.3f\ty%7.3f\tz:%7.3f\n",
                minuslinear.A,minuslinear.B,minuslinear.C,result.A,result.B,result.C);
        if (is_motion_changed()) {
            direction_angle = (float)( 180/3.1415926*Math.atan(result.B / result.A));//加速度方向夹角
            msg+= String.format("你在加速或减速\n手机坐标系：x:%7.3f\ty:%7.3f\tz:%7.3f\n" +
                            "地球坐标系：x:%7.3f\ty:%7.3f\tz:%7.3f\n方向与x方向夹角%7.3f\n",
                    minuslinear.A,minuslinear.B,minuslinear.C,result.A,result.B,result.C, direction_angle);
        }*/
        //process_text.setText(msg);
    }
    private void step_message_synthesis(){
        float distance = walker.step_count*walker.step_length/100;
        String msg=String.format("当前步数：%d\t所走路程:%7.2f米\n",walker.step_count,distance);
        String msg1="";
        if(ready_calculate) {
            if (current_time.size() > 100) {
                walk_count.clear();
                current_time.clear();
            }
            walk_count.add(walker.step_count);
            Date dt = new Date();
            current_time.add(dt.getTime());
            if (current_time.size() >= 2) {
                long time_interval = current_time.get(current_time.size() - 1) - current_time.get(current_time.size() - 2);
                msg1 = String.format("上次时间:%d\t当前时间:%d\n" +
                                "与上一步时间间隔为%d毫秒\n", current_time.get(current_time.size() - 2),
                        current_time.get(current_time.size() - 1), time_interval);
            }
        }
    }
    private void accclerate_analyse(){
            if(mobile_linear_accelerate.size()<Queue_Length)
                return;
             String msg1="";
            float earth_angle,mobile_angle;//分别为地球坐标系和手机坐标系下的加速度方向

            ThreeNums common_earth_accelerate=ThreeNums.average(earth_linear_accelerate);
            //ThreeNums av_mobile_accelerate =ThreeNums.my_average(mobile_linear_accelerate,
            //        accelerate_walking_limit,Queue_Length);
            ThreeNums av_mobile_accelerate=ThreeNums.average(mobile_linear_accelerate);
            ThreeNums av_earth_accelerate =getresult(av_mobile_accelerate,sensorManager);
            String msg0 = String.format("你在加速或减速\n手机坐标系：x:%7.3f\ty:%7.3f\tz:%7.3f\n" +
                        "地球坐标系：x:%7.3f\ty:%7.3f\tz:%7.3f\n",
                av_mobile_accelerate.A,av_mobile_accelerate.B,av_mobile_accelerate.C,
                av_earth_accelerate.A,av_earth_accelerate.B,av_earth_accelerate.C);
            process_text.setText(msg0);
            if (ThreeNums.abs_2d(common_earth_accelerate)>accelerate_walking_limit) {
                earth_angle = (float)( 180/3.1415926*Math.atan(av_earth_accelerate.B / av_earth_accelerate.A));
                if(av_earth_accelerate.A<0)
                    earth_angle+=180;
                mobile_angle = (float)( 180/3.1415926*Math.atan(av_mobile_accelerate.B /av_mobile_accelerate.A));

                float direction=get_direction(sensorManager);
                msg1=String.format("%d",data_count);
                liner_text.setText(msg1);
                Myview myview=new Myview(this,ThreeNums.abs_2d(av_earth_accelerate),earth_angle,direction);
                myview.invalidate();
                my_Canvas.removeAllViews();
                my_Canvas.addView(myview);
                times++;
                  if(times==10) {
                    times=0;
                      if(isPost) {
                          List<Float> info = new ArrayList<Float>();
                          info.add(av_mobile_accelerate.A);
                          info.add(av_mobile_accelerate.B);
                          info.add(av_mobile_accelerate.C);
                          info.add(av_earth_accelerate.A);
                          info.add(av_earth_accelerate.B);
                          info.add(av_earth_accelerate.C);
                          info.add(angle_yu(earth_angle));
                          info.add(angle_yu(90-direction));
                          postData(info);
                          data_count++;
                      }
                  }
                } else {
                //String msg0 = String.format("你在匀速前进\n");
                //liner_text.setText(msg+msg1+msg0);
                Myview myview=new Myview(this,0,0,0);
                myview.invalidate();
                my_Canvas.removeAllViews();
                my_Canvas.addView(myview);
            }
    }
    private void ThreeNums_addqueue(ThreeNums ONE,List<ThreeNums>a,int SIZE){//将采样值放入数组的函数
        if(a.size()<SIZE)a.add(ONE);
        else {
            a.clear();
            a.add(ONE);
        }
    }
    private String direction_justify(float angle){//根据角度判断方向的函数
        if(angle>=-67.5&&angle<-22.5)
            return "东南";
        else if(angle<22.5&&angle>=-22.5)
        return "东";
        else if(angle>=22.5&&angle<67.5)
            return "东北";
            else if(angle>=67.5&&angle<112.5)
            return "北";
        else if(angle>=112.5&&angle<157.5)
            return "西北";
        else if(angle>=157.5&&angle<202.5)
            return "西";
        else if(angle>=202.5&&angle<247.5)
            return "西南";
        else
            return "南";
    }
    private float angle_justify(float angle){//根据角度判断方向的函数
        if(angle>=-67.5&&angle<-22.5)
            return -45;
        else if(angle<22.5&&angle>=-22.5)
            return 0;
        else if(angle>=22.5&&angle<67.5)
            return 45;
        else if(angle>=67.5&&angle<112.5)
            return 90;
        else if(angle>=112.5&&angle<157.5)
            return 135;
        else if(angle>=157.5&&angle<202.5)
            return 180;
        else if(angle>=202.5&&angle<247.5)
            return 225;
        else
            return 270;

    }
    private void postData(List<Float>a) {
        //String url = "http://192.168.191.1:8000/disk/";
        String url = string_ip;
        Myrequest request = new Myrequest(a,Request.Method.POST,url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.i("aa", "post请求成功" + response);
                Toast.makeText(MainActivity.this, response, Toast.LENGTH_LONG).show();

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("aa", "post请求失败" + error.toString());
                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG).show();

            }
        });
        request.setTag("request");
        mQueue.add(request);
    }
    private void ispost(){
        if(button_post.getText()=="开始上传"){
            button_post.setText("停止上传");
            isPost=true;
        }
        else{
            button_post.setText("开始上传");
            isPost=false;
        }
    }
    private double[] transmit(float[]tran,double[] in){
        double[] out=new double[3];
        for(int i=0;i<3;i++){
            out[i]=tran[3*i]*in[0]+tran[3*i+1]*in[1]+tran[3*i+2]*in[2];
        }
        return out;
    }
    public ThreeNums getresult(ThreeNums IN,SensorManager manager){//获得原矢量在地球坐标系下的分量
        double[] result =new double[3];
        double[] in= {IN.A,IN.B,IN.C};
        float[] mR = new float[9];
        float[] mI = new float[9];
        float[] mOrientation = new float[3];
        float[] acele={gravity.A,gravity.B,gravity.C};
        float[] magnet={compass.A,compass.B,compass.C};
        manager.getRotationMatrix(mR, mI,acele,magnet);
        manager.getOrientation(mR,mOrientation);
        //手机绕z轴旋转的度数
        float azimuth = (float) Math.toDegrees(mOrientation[0]);
        //手机绕x轴旋转的度数
        float pitch = (float) Math.toDegrees(mOrientation[1]);
        //手机绕y轴旋转的度数
        float roll = (float) Math.toDegrees(mOrientation[2]);
        String msg=String.format("正东转角:%7.3f\t顶尾翘角:%7.3f\t左右转角:%7.3f\t",angle_yu(90-azimuth),pitch,roll);
        //linear_text.setText(msg);
       /* double cos_pha=Math.cos(mOrientation[0]);
        double sin_pha=Math.sin(mOrientation[0]);
        double cos_theta=Math.cos(mOrientation[1]);
        double sin_theta=Math.sin(mOrientation[1]);
        double cos_gama=Math.cos(mOrientation[2]);
        double sin_gama=Math.sin(mOrientation[2]);
        double[] tran1={cos_pha,sin_pha,0,-sin_pha,cos_pha,0,0,0,1};
        double[] tran2={1,0,0,0,cos_theta,sin_theta,0,-sin_theta,cos_theta};
        double[] tran3={cos_gama,0,-sin_gama,0,1,0,sin_gama,0,cos_gama};
        result=transmit(tran1,in);
        result=transmit(tran2,result);
        result=transmit(tran3,result);*/
        result=transmit(mR,in);
        return new ThreeNums((float) result[0],(float)result[1],(float)result[2]);
    }
    private float angle_yu(float angle){//将角度限制在-180~180范围内
        float out;
        out=angle;
        while(out<-180){
            out+=360;
        }
        while(out>180){
            out-=360;
        }
        return out;
    }
}