package com.example.chen.sesor;

/**
 * Created by chen on 2017/3/29.
 */

public class Walker { //记录用户的身高、步长、路程等信息
    public int step_count;
    public float height;
    public float distance;
    public float step_length;
    Walker(int step_count,float height,float step_length,float distance){
        super();
        this.step_count=step_count;
        this.distance=distance;
        this.height=height;
        this.step_length=step_length;
    }

}
