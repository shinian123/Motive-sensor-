package com.example.chen.sesor;

/**
 * Created by chen on 2017/3/26.
 */

public class Motion {
    public ThreeNums a;
    public ThreeNums v;
    public ThreeNums x;
    Motion(ThreeNums a,ThreeNums v,ThreeNums x){
        super();
        this.a=a;
        this.v=v;
        this.x=x;
    }
    static Motion getmotion(ThreeNums a,Motion mymotion,float dtime){
        Motion mynewmotion =mymotion;
        mynewmotion.a=a;
        mynewmotion.v=ThreeNums.plus(mymotion.v,ThreeNums.shucheng(a,dtime));
        mynewmotion.x=ThreeNums.plus(mymotion.x,ThreeNums.shucheng(mynewmotion.v,dtime));
        return mynewmotion;
    }
}
