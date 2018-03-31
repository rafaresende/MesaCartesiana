package com.necromato.app.mesacartesiana;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by Rafa on 23/10/2017.
 */

public class BluetoothService implements Runnable {

    private boolean BTConnected = false;
    private boolean stopThread = false;
    private InputStream inputStream;
    private OutputStream outputStream;
    private BluetoothAdapter myBluetooth = null;
    private BluetoothSocket btSocket = null;
    private byte buffer[];

    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public Handler mHandler;
    public Message msg;
    Bundle bundle = new Bundle();


    BluetoothService(Handler handler){
        mHandler = handler;
    }


    @Override
    public void run(){

        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

        while(!Thread.currentThread().isInterrupted() && !stopThread)
        {
            try
            {
                int byteCount = inputStream.available();
                if(byteCount > 0)
                {
                    byte[] rawBytes = new byte[byteCount];
                    inputStream.read(rawBytes);
                    final String string = new String(rawBytes,"UTF-8");
                    msg = mHandler.obtainMessage();
                    msg.obj = string;
                    mHandler.sendMessage(msg);
                    Thread.sleep(500);
                }

            }
            catch (IOException ex)
            {
                stopThread = true;
            //} catch (InterruptedException e) {
             //   e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public boolean Connect(String address){
        try
        {
            if (btSocket == null || !BTConnected)
            {
                myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                btSocket.connect();//start connection
                BTConnected = true;
                try {
                    outputStream=btSocket.getOutputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    inputStream=btSocket.getInputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        catch (IOException e)
        {
            BTConnected = false;//if the try failed, you can check the exception here
        }

        return BTConnected;
    }

    public void writeValue(String a) {
        try {
            outputStream.write(a.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disconnect(){
        try {
            btSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
