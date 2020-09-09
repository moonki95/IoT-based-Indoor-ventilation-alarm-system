#include <DHT.h>
#include <SoftwareSerial.h>
#define DHTPIN 6      // DHT핀을 2번으로 정의한다(DATA핀)
#define DHTTYPE DHT11  // DHT타입을 DHT11로 정의한다
DHT dht(DHTPIN, DHTTYPE);  // DHT설정 - dht (디지털2, dht11)

int Vo = A1; // 
int V_LED = 12;
float Vo_value = 0;
float Voltage = 0;

float dustDensity = 0;
float dustCleanVoltage = 0.44;
int samplingTime = 280;
int deltaTime = 40;
int sleepTime = 9680;


float humi;
float temp;
SoftwareSerial mySerial(2,3); // RX,TX

String ssid = "MK1";
String Pass = "ansrl123";
String host = "54.237.101.189";

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
  String cmd="GET /sensor2.php?temp="+url+" HTTP/1.0\r\n\r\n";
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
  pinMode(V_LED, OUTPUT);
  /*analog input:*/
  pinMode(Vo, INPUT);
  connectWifi();
  delay(500);
}

void loop(){
  digitalWrite(V_LED,LOW); //ired 'on'
  delayMicroseconds(samplingTime);
  Vo_value = analogRead(Vo); //read the dust value
  delayMicroseconds(deltaTime);// pulse width 0.32 - 0.28 = 0.04 msec
  //0.32 msec의 펄스폭을 유지하기 위해 필요한 코드입니다
  
  digitalWrite(V_LED,HIGH); //ired 'off'
  delayMicroseconds(sleepTime);

  Voltage = Vo_value * (5.0 / 1024.0);
  dustDensity = (Voltage - dustCleanVoltage)/0.005;
  humi = dht.readHumidity();
  temp = dht.readTemperature();
  String str_output = String(temp)+"&humi="+String(humi)+"&dustDensity="+String(dustDensity);
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
