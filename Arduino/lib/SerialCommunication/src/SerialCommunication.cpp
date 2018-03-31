#include "Arduino.h"
#include "SerialCommunication.h"

SerialCommunication :: SerialCommunication (unsigned long baudrate, unsigned long sendInterval) {
  Serial.begin(baudrate);
  _newData = false;
  _sendTimer = micros();
  _sendInterval = sendInterval;
  _inputString = "";
}

String SerialCommunication :: receiveData() {

  _newData = false;

  static byte ndx = 0;
  char endMarker = '\n';
  char rc;

  while (Serial.available() > 0 && _newData == false) {

    rc = Serial.read();

    if (rc != endMarker) {
      receivedChars[ndx] = rc;
      ndx++;
      if (ndx >= numChars) {
        ndx = numChars - 1;
      }
    }
    else {
      receivedChars[ndx] = '\0';
      _inputString = receivedChars;// terminate the string
      ndx = 0;
      _newData = true;
      return _inputString;

    }
  }

  return "";

}

void SerialCommunication :: sendData(String data) {
  if ( (micros () - _sendTimer) >= _sendInterval) {

     _sendTimer = micros ()  ;
     Serial.println(data);
  }
}
