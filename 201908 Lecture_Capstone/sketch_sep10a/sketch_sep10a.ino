#include <SPI.h>

#define CS 3 //Chip or Slave select 
 
uint16_t ABSposition = 0;
uint16_t ABSposition_last = 0;
uint8_t temp[2];    //This one.

 
void setup()
{
  pinMode(CS,OUTPUT);//Slave Select
  digitalWrite(CS,HIGH);
  SPI.begin();
  SPI.setBitOrder(MSBFIRST);
  SPI.setDataMode(SPI_MODE0);
  SPI.setClockDivider(SPI_CLOCK_DIV32);
  Serial.begin(115200);
  Serial.flush();
  delay(200);
  SPI.end();
 
}
uint8_t SPI_T (uint8_t msg)    //Repetive SPI transmit sequence
{
   uint8_t msg_temp = 0;  //vairable to hold recieved data
   digitalWrite(CS,LOW);     //select spi device
   msg_temp = SPI.transfer(msg);    //send and recieve
   digitalWrite(CS,HIGH);    //deselect spi device
   return(msg_temp);      //return recieved byte
}
 
void loop()
{ 
   uint8_t recieved = 0xA5;    //just a temp vairable
   ABSposition = 0;    //reset position vairable
   
   SPI.begin();    //start transmition
   digitalWrite(CS,LOW);
   
   SPI_T(0x10);   //issue read command
   
   recieved = SPI_T(0x00);    //issue NOP to check if encoder is ready to send
   
   while (recieved != 0x10)    //loop while encoder is not ready to send
   {
     recieved = SPI_T(0x00);    //cleck again if encoder is still working 
     delay(2);    //wait a bit
   }
   
   temp[0] = SPI_T(0x00);    //Recieve MSB
   temp[1] = SPI_T(0x00);    // recieve LSB
   
   digitalWrite(CS,HIGH);  //just to make sure   
   SPI.end();    //end transmition

   temp[0] &=~ 0xF0; 
   ABSposition = temp[0] << 8;    //shift MSB to correct ABSposition in ABSposition message
   ABSposition += temp[1];    // add LSB to ABSposition message to complete message
    
   //if (ABSposition != ABSposition_last)    //if nothing has changed dont wast time sending position
   //{
     ABSposition_last = ABSposition;    //set last position to current position
     
     //Serial.write(tx, 2);
     int a = ABSposition / 1000;
     int b = (ABSposition % 1000)/100;
     int c = ((ABSposition % 1000)%100)/10;
     int d = ((ABSposition % 1000)%100)%10;

     String tx = String(a)+ String(b)+ String(c)+ String(d);
     //Serial.print(tx);
     Serial.print(tx);
   //}   
    
   delay(10);    //wait a bit till next check
 
}
