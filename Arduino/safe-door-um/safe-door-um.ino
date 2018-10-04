#include <Servo.h>

// Pins
int servoPin = 3;
int redLedPin = 4;
int greenLedPin = 6;
int inputButton = 12;
// Values
int servoOpen = 30;
int servoClose = 120;
int inputButtonValue = 0;
// Vars
Servo servoInstance;
int servoPosition;
int btMessage;

void setup() {
  Serial.begin(9600);
  pinMode(redLedPin, OUTPUT);
  pinMode(greenLedPin, OUTPUT);
  pinMode(inputButton, INPUT);
  servoInstance.attach(servoPin); // 650, 2550
  delay(250);

  servoPosition = servoInstance.read();
  if (servoPosition < servoClose) {
    closeDoor();
  }

  Serial.println(servoPosition);
}

void loop() {
  inputButtonValue = digitalRead(inputButton);

  if (Serial.available() > 0) {
    btMessage = Serial.read();
  }

  if (inputButtonValue == HIGH || btMessage == 'a') {
    if (servoPosition > servoOpen) {
      openDoor();
    }
  } else if (inputButtonValue == LOW || btMessage == 'b') {
    if (servoPosition < servoClose) {
      closeDoor();
    }
  }

  delay(250);
}

void openDoor() {
  servoInstance.write(servoOpen);
  delay(200);
  servoPosition = servoInstance.read();
  digitalWrite(greenLedPin, HIGH);
  digitalWrite(redLedPin, LOW);
}

void closeDoor() {
  servoInstance.write(servoClose);
  delay(200);
  servoPosition = servoInstance.read();
  digitalWrite(greenLedPin, LOW);
  digitalWrite(redLedPin, HIGH);
}
