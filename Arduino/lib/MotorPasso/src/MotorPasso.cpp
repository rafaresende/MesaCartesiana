#include "Arduino.h"
#include "MotorPasso.h"

MotorPasso :: MotorPasso (int passosPorVolta, int dirPin, int stepPin, int sensorPin) {
  pinMode(dirPin, OUTPUT);
  _dirPin = dirPin;

  pinMode(stepPin, OUTPUT);
  _stepPin = stepPin;

  pinMode(sensorPin, INPUT);
  _sensorPin = sensorPin;

  _passosPorVolta = passosPorVolta;

  _period = 1000000;

  _enable = false;
  _reverse = false;

  _pulses = 0;
  _elapsedTime = micros();
}

void MotorPasso :: moveTo(int pos) {

  // set the direction of the motor
  reverse(pos);

  if (_pulses != pos){
     enable();
  } else {
     disable();
  }

}

void MotorPasso :: reverse(int desejado) {

  if (_pulses > desejado){
    _reverse = true;
    digitalWrite (_dirPin, HIGH) ;
    return;
  }digitalWrite (_dirPin, HIGH) ;

  _reverse = false;
  digitalWrite (_dirPin, LOW) ;

}

void MotorPasso :: enable(){

  if ( (micros () - _elapsedTime) >= _period) {
    // It is time to change state. Calculate next state.
    if (_state == LOW) {
      _state = HIGH ;
      //increment the pulse counter
      if(!_reverse){
        _pulses += 1;
      } else {
        _pulses -= 1;
      }

    } else {
      _state = LOW ;
    }
    // Write new state
    digitalWrite (_stepPin, _state ) ;
    // Reset timer
    _elapsedTime = micros () ;
  }

}

void MotorPasso :: disable () {
  _state = LOW;
  digitalWrite (_stepPin, _state) ;
}

void MotorPasso :: setPeriod(unsigned long period){
  _period = period;
}

long MotorPasso :: getPulses(){
  return _pulses;
}

void MotorPasso :: setPulses(long pulso){
  _pulses = pulso;
}

bool MotorPasso :: goZero(){
  while (digitalRead(_sensorPin)!=LOW){
    setDir(true);
    enable();
    return false;
  }
  disable();
  return true;
}

void MotorPasso :: setDir(bool dir){
  _reverse = dir;
  if(_reverse){
    digitalWrite (_dirPin, HIGH) ;
  } else {
    digitalWrite (_dirPin, LOW) ;
  }
}
