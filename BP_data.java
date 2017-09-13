package com.example.chen.sesor;

import java.util.List;

/**
 * Created by chen on 2017/5/15.
 */

public class BP_data {
    private List<Double> data_x;          //输入输出数据
    private List<Double> data_y;
    BP_data(List<Double>x,List<Double>y){
        super();
        this.data_x=x;
        this.data_y=y;
    }
}
