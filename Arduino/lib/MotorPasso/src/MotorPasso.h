/*
  MotorPasso.h - Library for controlling a stepper motor
  Created by Rafael R. Marcondes. November 2, 2017
*/

#ifndef MotorPasso_h
#define MotorPasso_h

    #include "Arduino.h"

    class MotorPasso {
    private:

      //Define os pinos usados
      int _stepPin;
      int _dirPin;
      int _sensorPin;


      int _passosPorVolta;
      int _posDes;
      unsigned long _period;
      bool _enable;
      bool _reverse;
      long _pulses;
      int _state;
      unsigned long _elapsedTime;

      void reverse(int pos);


    public:
      MotorPasso (int passosPorVolta, int dirPin, int stepPin, int sensorPin);
      void moveTo(int pos);
      void enable();
      void disable();
      void setPeriod(unsigned long period);
      long getPulses();
      void setPulses(long pulso);
      bool goZero();
      void setDir(bool dir);
    };

#endif
