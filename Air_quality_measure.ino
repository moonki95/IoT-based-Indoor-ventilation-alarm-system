#define DHTPIN A3      // DHT핀을 2번으로 정의한다(DATA핀)
#define DHTTYPE DHT11  // DHT타입을 DHT11로 정의한다
#include "DHT.h"       // DHT.h 라이브러리를 포함한다

#include <pm2008_i2c.h>
#include <SoftwareSerial.h>
SoftwareSerial mySerial(2,3); // RX,TX
DHT dht(DHTPIN, DHTTYPE);  // DHT설정 - dht (디지털6, dht11)
PM2008_I2C dust;

int Vo = A1; // 
int V_LED = 12;
float Vo_value = 0;
float Voltage = 0;

float dustDensity2 = 0;
float dustDensity = 0;
float dustCleanVoltage = 0.44;
int samplingTime = 280;
int deltaTime = 40;
int sleepTime = 9680;

String ssid = "";
String Pass = "";
String host = "";

void connectWifi(){
  
 String m_reset = "AT+RST";
 mySerial.println(m_reset);
 String join = "AT+CWJAP=\""+ssid+"\",\""+Pass+"\"";
 Serial.println("Connect Wifi...");
 mySerial.println(join);
 delay(10000);
 if(mySerial.find("OK")){
   Serial.print("WIFI connect\n");
 }
 else{
   Serial.println("Connect timeout\n");
 }
 delay(1000);
}

void httpclient(String char_input){
  delay(100);
  Serial.println("connect TCP...");
  mySerial.println("AT+CIPSTART=\"TCP\",\""+host+"\",80");
  delay(500);
  if(Serial.find("ERROR"))
    return;
  Serial.println("Send data...");
  String url=char_input;
  String cmd="GET /sensor.php?temperature="+url+" HTTP/1.0\r\n\r\n";

  mySerial.print("AT+CIPSEND=");
  mySerial.println(cmd.length());
  Serial.print("AT+CIPSEND=");
  Serial.println(cmd.length());
  if(mySerial.find(">")){
    Serial.print(">");
  }
  else{
    mySerial.println("AT+CIPCLOSE");
    Serial.println("connect timeout");
    delay(1000);
    return;
  }
  delay(500);

  mySerial.println(cmd);
  Serial.println(cmd);
  delay(100);
  if(Serial.find("ERROR")){
    return;
  }
  mySerial.println("AT+CIPCLOSE");
  delay(100);
}

void setup(){
  
  Serial.begin(9600);
  mySerial.begin(9600);
  connectWifi();
  dust.begin();
  dust.read();
  dust.command();
  delay(500);
}

void loop(){

  int dust_density = dust.pm10_tsi;
  int humidity = dht.readHumidity();  // 변수 h에 습도 값을 저장 
  int temperature = dht.readTemperature();  // 변수 t에 온도 값을 저장
  
  String str_output = String(temperature)+"&humidity="+String(humidity)+"&dust_density="+String(dust_density);
  delay(1000);
  httpclient(str_output);
  delay(1000);

  while(mySerial.available()){
    char response = mySerial.read();
    Serial.write(response);
    if(response=='\r'){
      Serial.println("\n");
    }
  }
  Serial.println("\n===========================\n");
  delay(2000);
  
}
