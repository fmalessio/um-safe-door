int led4 = 4;
int estado = 0;

void setup() {
  Serial.begin(9600);
  pinMode(led4, OUTPUT);
}

void loop() {
  if ( Serial.available() > 0) {
    estado = Serial.read();
  }
  switch (estado) {
    case 'a':
      digitalWrite(led4, HIGH);
      break;
    case 'b':
      digitalWrite(led4, LOW);
      break;
  }
}
