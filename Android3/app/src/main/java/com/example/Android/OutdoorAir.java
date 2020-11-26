package com.example.Android;

public class OutdoorAir {
    public static String sido=null;
    public static String gungu;
    public static String dataTime;
    public static String PM10;
    public static String PM25;
    public static String o3;
    public static String co;
    public static String no2;
    public static String so2;
    public static String PM10Grade;
    public static String PM25Grade;

    public static void setSido(String str){
        sido=str;
    }
    public static void setGungu(String str){
        gungu=str;
    }
    public static void setDataTime(String str){
        dataTime=str.substring(str.length()-5,str.length());
    }
    public static void setPM10(String s){
        PM10=s;
    }
    public static void setPM25(String s){
        PM25=s;
    }
    public static void setO3(String s){  o3=s;   }
    public static void setCo(String s){ co=s;    }
    public static void setNo2(String s){
        no2=s;
    }
    public static void setSo2(String s){
        so2=s;
    }

    public static String getSido(){
        if(sido==null) return null;
        else return sido;
    }
    public static String getGungu(){
        return gungu;
    }
    public static String getDataTime( ){
       return dataTime;
    }
    public static String getPM10( ){
       return PM10;
    }
    public static String getPM25( ){
       return PM25;
    }
    public static String getO3( ){
        return o3;
    }
    public static String getCo( ){
        return co;
    }
    public static String getNo2( ){
        return no2;
    }
    public static String getSo2( ){
        return so2;
    }
    public static String getPM10Grade() {

        int pm10 = Integer.parseInt(PM10);
        if (pm10 <= 30) return"좋음";
        else if (pm10 <= 80) return "보통";
        else if (pm10 <= 150) return "나쁨";
        else return "매우나쁨";
    }
    public static String getPM25Grade(){
        int pm25 = Integer.parseInt(PM25);
        if (pm25 <= 15) return"좋음";
        else if (pm25 <= 50) return "보통";
        else if (pm25 <= 100) return "나쁨";
        else return "매우나쁨";
    }
}
