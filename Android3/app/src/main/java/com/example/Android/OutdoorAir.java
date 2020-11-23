package com.example.Android;

public class OutdoorAir {
    public static String sido;
    public static String gungu;
    public static String dataTime;
    public static String PM10;
    public static String PM25;
    public static String PM10Grade;
    public static String PM25Grade;

    public static void setSido(String str){
        sido=str;
    }
    public static void setGungu(String str){
        gungu=str;
    }
    public static void setDataTime(String str){
        dataTime=str;
    }
    public static void setPM10(String pm10){
        PM10=pm10;
    }
    public static void setPM25(String pm25){
        PM25=pm25;
    }

    public static String getSido(){
        return sido;
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
    public static String getPM10Grade() {

        int pm10 = Integer.parseInt(PM10);
        if (pm10 <= 30) return"좋음";
        else if (pm10 <= 80) return "보통";
        else if (pm10 <= 120) return "약간나쁨";
        else return "나쁨";
    }
    public static String getPM25Grade(){
        int pm25 = Integer.parseInt(PM25);
        if (pm25 <= 15) return"좋음";
        else if (pm25 <= 50) return "보통";
        else if (pm25 <= 100) return "나쁨";
        else return "매우나쁨";
    }
}
