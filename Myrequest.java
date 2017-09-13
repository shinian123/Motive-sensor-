package com.example.chen.sesor;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chen on 2017/4/26.
 */

public class Myrequest extends StringRequest {
    public List<Float> a;
    Myrequest(List<Float> a,int method,String url,Response.Listener<String> listener,Response.ErrorListener errorListener){
        super(method,url,listener,errorListener);
        this.a=a;
    }
    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        // 请求参数
        List<String>b=new ArrayList<String>();
        String key[]={"max","may","maz","ax","ay","az","angle","real_angle"};
        String str;
        for(int i=0;i<a.size();i++){
            str=String.format("%5.3f",a.get(i));
            b.add(str);
        }
        Map<String, String> map = new HashMap<String, String>();
        //new 一个Map  参数放到Map中
        for(int i=0;i<b.size();i++){
            map.put(key[i],b.get(i));
        }
        return map;
    }
}
