/*********************************************************************
  * Laura Arjona. PMPEE590
  * Example of simple interaction beteween Adafruit Circuit Playground
  * and Android App. Communication with BLE - uart
*********************************************************************/
#include <Arduino.h>
#include <SPI.h>
#include "Adafruit_BLE.h"
#include "Adafruit_BluefruitLE_SPI.h"
#include "Adafruit_BluefruitLE_UART.h"
#include <Adafruit_CircuitPlayground.h>

#include "BluefruitConfig.h"

#if SOFTWARE_SERIAL_AVAILABLE
  #include <SoftwareSerial.h>
#endif
// Strings to compare incoming BLE messages
String start = "start";
String green = "green";
String blue = "blue";
String red = "red";
String yellow = "yellow";
String readtemp = "readtemp";
String on = "on";
String off = "off";
String stp = "stop";

bool buzzer = false;
bool timerOn = false;

struct RGB {
  byte r;
  byte g;
  byte b;
};

float X, Y, Z;
struct accel {
  float shake;
  float Y;
  float Z;
};

RGB variable = { 255 , 255 , 255 };

int  sensorTemp = 0;


//Constants definition
//It is a good practice use capital letters to define constants

const int SHAKE_THRESHOLD = 20;
const unsigned int BLINK_DURATION=2000;

//Variables definition   
unsigned long timer_duration = 5000; //Timer duration of 5secondas    
unsigned long timer_step = timer_duration / 10; //We want to use 10 LEDs for the timer                
bool right_butt_state;
unsigned int led_number;
unsigned long stime;
unsigned long interval;
unsigned long butt_press_time;

  

/*=========================================================================
    APPLICATION SETTINGS
    -----------------------------------------------------------------------*/
    #define FACTORYRESET_ENABLE         0
    #define MINIMUM_FIRMWARE_VERSION    "0.6.6"
    #define MODE_LED_BEHAVIOUR          "MODE"
/*=========================================================================*/

// Create the bluefruit object, either software serial...uncomment these lines

Adafruit_BluefruitLE_UART ble(BLUEFRUIT_HWSERIAL_NAME, BLUEFRUIT_UART_MODE_PIN);

/* ...hardware SPI, using SCK/MOSI/MISO hardware SPI pins and then user selected CS/IRQ/RST */
// Adafruit_BluefruitLE_SPI ble(BLUEFRUIT_SPI_CS, BLUEFRUIT_SPI_IRQ, BLUEFRUIT_SPI_RST);

/* ...software SPI, using SCK/MOSI/MISO user-defined SPI pins and then user selected CS/IRQ/RST */
//Adafruit_BluefruitLE_SPI ble(BLUEFRUIT_SPI_SCK, BLUEFRUIT_SPI_MISO,
//                             BLUEFRUIT_SPI_MOSI, BLUEFRUIT_SPI_CS,
//                             BLUEFRUIT_SPI_IRQ, BLUEFRUIT_SPI_RST);


// A small helper
void error(const __FlashStringHelper*err) {
  Serial.println(err);
  while (1);
}

accel getTotalAccel();
/**************************************************************************/
/*!
    @brief  Sets up the HW an the BLE module (this function is called
            automatically on startup)
*/
/**************************************************************************/
void setup(void)
{
  CircuitPlayground.begin();
  

  Serial.begin(115200);
  Serial.println(F("Adafruit Bluefruit Command <-> Data Mode Example"));
  Serial.println(F("------------------------------------------------"));

  /* Initialise the module */
  Serial.print(F("Initialising the Bluefruit LE module: "));

  if ( !ble.begin(VERBOSE_MODE) )
  {
    error(F("Couldn't find Bluefruit, make sure it's in CoMmanD mode & check wiring?"));
  }
  Serial.println( F("OK!") );

  if ( FACTORYRESET_ENABLE )
  {
    /* Perform a factory reset to make sure everything is in a known state */
    Serial.println(F("Performing a factory reset: "));
    if ( ! ble.factoryReset() ){
      error(F("Couldn't factory reset"));
    }
  }

  /* Disable command echo from Bluefruit */
  ble.echo(false);

  Serial.println("Requesting Bluefruit info:");
  /* Print Bluefruit information */
  ble.info();

  Serial.println(F("Please use Adafruit Bluefruit LE app to connect in UART mode"));
  Serial.println(F("Then Enter characters to send to Bluefruit"));
  Serial.println();

  ble.verbose(false);  // debug info is a little annoying after this point!

  /* Wait for connection */
  while (! ble.isConnected()) {
      delay(500);
  }

  Serial.println(F("******************************"));

  // LED Activity command is only supported from 0.6.6
  if ( ble.isVersionAtLeast(MINIMUM_FIRMWARE_VERSION) )
  {
    // Change Mode LED Activity
    Serial.println(F("Change LED activity to " MODE_LED_BEHAVIOUR));
    ble.sendCommandCheckOK("AT+HWModeLED=" MODE_LED_BEHAVIOUR);
  }

  // Set module to DATA mode
  Serial.println( F("Switching to DATA mode!") );
  ble.setMode(BLUEFRUIT_MODE_DATA);

  Serial.println(F("******************************"));

  CircuitPlayground.setPixelColor(0,255,255,255);
 
  delay(1000);
}
/**************************************************************************/
/*!
    @brief  Constantly poll for new command or response data
*/
/**************************************************************************/
void loop(void)
{

/*if(getTotalAccel()>SHAKE_THRESHOLD)
  {
  Serial.println("shake");  
  char output[8];
  String data = "";
  data = "shake";
  Serial.println(data);
  data.toCharArray(output,8);
  ble.print(data);
  }*/
  X = CircuitPlayground.motionX();
  Y = CircuitPlayground.motionY();
  Z = CircuitPlayground.motionZ();

  /*Serial.print(getX()); Serial.print("\t");
  Serial.print(getY()); Serial.print("\t");
  Serial.print(getZ()); Serial.println();
  delay(10);
*/
 accel m = getTotalAccel();
  if(m.shake>20){
      char output[8];
      String data = "";
      data = "shake";
      Serial.println(data);
      data.toCharArray(output,8);
      ble.print(data);
      delay(1000);
    }
  if(m.Z<4 && m.Y>8){  //right tilt
    if(Y<-8 && X<2){
      char output[8];
      String data = "";
      data = "next";
      Serial.println(data);
      data.toCharArray(output,8);
      ble.print(data);
      delay(1000);
      }
    else if(Y>8 && X<2){  //left tilt
      char output[8];
      String data = "";
      data = "prev";
      Serial.println(data);
      data.toCharArray(output,8);
      ble.print(data);
      delay(1000);
      }
    
    }
}

accel getTotalAccel()
{ 
  // Compute total acceleration 
  float X = 0; 
  float Y = 0; 
  float Z = 0; 
  accel val;
  for (int i=0; i<10; i++) 
  { 
  X += CircuitPlayground.motionX(); 
  Y += CircuitPlayground.motionY(); 
  Z += CircuitPlayground.motionZ(); 
  delay(1); } 
  X /= 10; 
  Y /= 10; 
  Z /= 10; 
  
  float a = sqrt(X*X + Y*Y + Z*Z);
  val.shake = a;
  val.Y = sqrt(Y*Y);
  val.Z = sqrt(Z*Z);
 
 // Serial.println(accel);
 
  //delay(100);
  return val;
  }

  



 
