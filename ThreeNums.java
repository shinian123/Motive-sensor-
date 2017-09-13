package com.example.chen.sesor;

import java.util.List;

/**
 * Created by chen on 2017/3/25.
 */

public class ThreeNums {
    public float A;
    public float B;
    public float C;
    ThreeNums(float A,float B,float C){
        super();
        this.A=A;
        this.B=B;
        this.C=C;
    }
    static ThreeNums average(List<ThreeNums> nums){
        float totalA=0;
        float totalB=0;
        float totalC=0;
        for (int i=0;i<nums.size();i++){
            totalA=totalA+nums.get(i).A;
            totalB=totalB+nums.get(i).B;
            totalC=totalC+nums.get(i).C;
        }
        return new ThreeNums(totalA/nums.size(),totalB/nums.size(),totalC/nums.size());
    }
    static ThreeNums variance(List<ThreeNums> nums){
        float totalA=0;
        float totalB=0;
        float totalC=0;
        ThreeNums nums_average=average(nums);
        for (int i=0;i<nums.size();i++){
            totalA=totalA+(float) Math.pow(nums.get(i).A-nums_average.A,2);
            totalB=totalB+(float) Math.pow(nums.get(i).B-nums_average.B,2);
            totalC=totalC+(float) Math.pow(nums.get(i).C-nums_average.C,2);
        }
        return new ThreeNums(totalA/nums.size(),totalB/nums.size(),totalC/nums.size());
    }
    static ThreeNums minus(ThreeNums ONE,ThreeNums TWO){
        return new ThreeNums(ONE.A-TWO.A,ONE.B-TWO.B,ONE.C-TWO.C);
    }
    static ThreeNums plus(ThreeNums ONE,ThreeNums TWO){
        return new ThreeNums(ONE.A+TWO.A,ONE.B+TWO.B,ONE.C+TWO.C);
    }
    static float dianji(ThreeNums ONE,ThreeNums TWO){
        return  (ONE.A*TWO.A+ONE.B*TWO.B+ONE.C*TWO.C);
    }
    static ThreeNums chaji(ThreeNums ONE,ThreeNums TWO){
        float out0,out1,out2;
        out0=ONE.B*TWO.C-ONE.C*TWO.B;
        out1=ONE.C*TWO.A-ONE.A*TWO.C;
        out2=ONE.A*TWO.B-ONE.B*TWO.A;
        return new ThreeNums(out0,out1,out2);
    }
    static float mo(ThreeNums ONE){
        double a;
        a=Math.sqrt(Math.pow(ONE.A,2)+Math.pow(ONE.B,2)+Math.pow(ONE.C,2));
        return (float)a;
    }
    static float abs_2d(ThreeNums ONE){
        double a;
        a=Math.sqrt(Math.pow(ONE.A,2)+Math.pow(ONE.B,2));
        return (float)a;
    }
    static ThreeNums getsum(ThreeNums a0,List<ThreeNums>b,float time){
        ThreeNums result=new ThreeNums(0,0,0);
        for(int i=0;i<b.size();i++){
            result.A+=b.get(i).A*time;
            result.B+=b.get(i).B*time;
            result.C+=b.get(i).C*time;
        }
        return result;
    }
    static ThreeNums form(float A,float B,float C){
        return new ThreeNums(A,B,C);
    }
    static ThreeNums shucheng(ThreeNums ONE,float a){
        return new ThreeNums(a*ONE.A,a*ONE.B,a*ONE.C);
    }

    static ThreeNums my_average(List<ThreeNums>a,float limit,int count){
        int valid_count=0;
        float x=0,y=0,z=0;
        for(int i=a.size()-1;i>=0;i--) {
            if (abs_2d(a.get(i)) >= limit) {
                x += a.get(i).A;
                y += a.get(i).B;
                z += a.get(i).C;
                valid_count++;
            }
            if (valid_count >= count) {
                break;
            }
        }
        if(valid_count>0)
        return new ThreeNums(x/valid_count,y/valid_count,z/valid_count);
        else
            return a.get(a.size()-1);
    }
}
