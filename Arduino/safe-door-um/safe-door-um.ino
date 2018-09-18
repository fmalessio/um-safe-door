#include <Servo.h>

int ledPin = 4;
int servoPin = 3;
int servoOpen = 30;
int servoClose = 120;
Servo servoInstance;
int servoPosition;
int btMessage;

void setup() {
  Serial.begin(9600);  
  pinMode(ledPin, OUTPUT);
  servoInstance.attach(servoPin); // 650, 2550
  delay(250);

  servoPosition = servoInstance.read();
  if(servoPosition < servoClose) {
    servoInstance.write(servoClose);
  }
  Serial.println(servoPosition);
}

void loop() {
  if (Serial.available() > 0) {
    btMessage = Serial.read();
  }
  switch (btMessage) {
    case 'a':
      if(servoPosition > servoOpen) {
        servoInstance.write(servoOpen);
        delay(200);
        servoPosition = servoInstance.read();
        digitalWrite(ledPin, HIGH);
      }
      break;
    case 'b':
      if(servoPosition < servoClose) {
        servoInstance.write(servoClose);
        delay(200);
        servoPosition = servoInstance.read();
        digitalWrite(ledPin, LOW);
      }
      break;
  }
  delay(250);
}
