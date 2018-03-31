#include "Arduino.h"
#include "SerialCommunication.h"
#include "MotorPasso.h"

SerialCommunication serial1(9600, 300000);
MotorPasso motorX(200, 2, 11, 6);
MotorPasso motorY(200, 3, 5, 7);

String inputString = "";

long xDesPos = 0;
long yDesPos = 0;

long xAtual;
long yAtual;

double velocidade = 0;

bool manualX = false;
bool manualY = false;
bool stop = false;
bool ori = false;


void setup() {
    // put your setup code here, to run once:
    Serial.begin(9600);
    //motorX.goZero();
    //motorY.goZero();
}

void loop() {
    // put your main code here, to run repeatedly:

    inputString = serial1.receiveData();

    if(inputString.startsWith("X")){
      xDesPos = inputString.substring(1).toInt();

    }  else if (inputString.startsWith("Y")) {
      yDesPos = inputString.substring(1).toInt();

    } else if (inputString.startsWith("VEL")) {

      velocidade = inputString.substring(3).toDouble();

      double espera = round(1000000/(100*2*velocidade));

      motorY.setPeriod((long) espera);
      motorX.setPeriod((long) espera);

    } else if (inputString.startsWith("AV")) {

      velocidade = inputString.substring(2).toDouble();
      double espera = round(1000000/(100*2*velocidade));

      double coefX = abs(xAtual - xDesPos);
      double coefY = abs(yAtual - yDesPos);

      double coef = 0;

            if (coefX != 0 && coefY != 0) {
                coef = coefX/coefY;
                if (coef < 1){
                    espera = espera * 1/coef;
                }
            } else if (coefX == 0) {
                coef = 1;
            } else if (coefY == 0) {
                coef = 0;
            }

            motorY.setPeriod(round(espera * coef));
            motorX.setPeriod((long) espera);

            Serial.println(coef);
            Serial.println(espera);

            manualX = false;
            manualY = false;
            stop = false;
            ori = false;

    }  else if (inputString.startsWith("MYP")) {
      stop = false;
        manualY = true;
        manualX = false;

        motorY.setDir(false);

    } else if (inputString.startsWith("MYN")) {
      stop = false;
      manualY = true;
      manualX = false;

      motorY.setDir(true);

    } else if (inputString.startsWith("MXP")) {
      stop = false;
      manualX = true;
      manualY = false;

      motorX.setDir(false);

    } else if (inputString.startsWith("MXN")) {
      stop = false;
      manualX = true;
      manualY = false;

      motorX.setDir(true);

    } else if (inputString.startsWith("GO_0")) {
      stop = false;
      ori = true;
    } else if (inputString.startsWith("STOP")) {
      stop = true;
      manualY = false;
      manualX = false;
      motorX.disable();
      motorY.disable();
      Serial.println(inputString);
    }

    yAtual = motorY.getPulses();
    xAtual = motorX.getPulses();

    if(!manualY && !manualX && !ori && !stop) {
      motorX.moveTo(xDesPos);
      motorY.moveTo(yDesPos);
    } else if (manualY) {
      motorY.enable();
    } else if (manualX) {
      motorX.enable();
    } else if (ori) {
      motorX.goZero();
      motorY.goZero();
    }


    //Envia posicao atual
    String Y = "Y";
    String A = xAtual + Y + yAtual;
    serial1.sendData(A);

}
