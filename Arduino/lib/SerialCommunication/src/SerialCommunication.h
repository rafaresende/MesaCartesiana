/*
  MesaCartesiana.h - Library for controlling a cartesian table
  Created by Rafael R. Marcondes. November 2, 2017
*/

#ifndef SerialCommunication_h
#define  SerialCommunication_h

    #include "Arduino.h"

    class SerialCommunication {
    private:

      unsigned long _sendTimer;
      unsigned long _sendInterval;
      String _inputString;
      bool _newData;

      const byte numChars = 32;
      char receivedChars[32];

    public:
      SerialCommunication (unsigned long baudrate, unsigned long sendInterval);
      String receiveData();
      void sendData(String data);
    };
#endif
