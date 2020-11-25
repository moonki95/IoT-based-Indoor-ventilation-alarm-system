package com.example.Android;
import java.lang.*;

public class IndoorAir {
    public static String Date;
    public static String temp;
    public static String humid;
    public static String dust;
    public static String dustGrade;

    public static void setDate(String str){
        Date=str;
    }
    public static void setTemp(String str){
        temp=str;
    }
    public static void setHumid(String str){
        humid=str;
    }
    public static void setDust(String str) {dust = str; }

    public static String getDate(){
        return Date;
    }
    public static String getTemp(){
        return temp;
    }
    public static String getHumid( ){
        return humid;
    }
    public static String getDust() { return dust; }
    public static String getDustGrade(){
        int d = Integer.parseInt(dust);
        if (d <= 30) dustGrade = "좋음";
        else if (d <= 80) dustGrade = "보통";
        else if (d <= 150) dustGrade = "나쁨";
        else dustGrade = "매우나쁨";
        return dustGrade;
    }
}
