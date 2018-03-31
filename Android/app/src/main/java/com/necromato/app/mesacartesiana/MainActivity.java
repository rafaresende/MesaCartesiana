package com.necromato.app.mesacartesiana;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    //TODO: teste
    Button Y_pos, Y_neg, X_pos, X_neg, origem, enviar, stopButton;
    String address = "";
    TextView xAtual;
    TextView yAtual;

    EditText xDesejado;
    EditText yDesejado;
    EditText vel;

    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //SPP UUID

    static final String standardAddress = "98:D3:32:30:E3:74";
    public Handler mHandler;
    public static BluetoothService BT;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.des:
                Intent i = new Intent(MainActivity.this, DrawPath.class);
                startActivity(i);
                return true;
            case R.id.desconnect:
                Disconnect();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //Recebe o endereco bluetooth do driver
        Intent newint = getIntent();
        address = newint.getStringExtra(PairedDevices.EXTRA_ADDRESS);

        android.support.v7.app.ActionBar A = getSupportActionBar();

        A.setSubtitle(address.toString());

        setContentView(R.layout.activity_main); //Seleciona o layout

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                // Act on the message[
                try {
                    String string = msg.obj.toString();
                    String[] parts = string.split("Y");
                    String part1 = parts[0]; // 004
                    String part2 = parts[1]; // 034556
                    xAtual.setText(part1);
                    yAtual.setText(part2);
                } catch (IndexOutOfBoundsException e){

                }
            }
        };

        BT = new BluetoothService(mHandler);

        if (BT.Connect(address))  msg("Connectado");

        Thread newThread = new Thread(BT);
        newThread.start();

        //Botoes
        enviar = (Button)findViewById(R.id.enviarCoord);
        Y_pos = (Button)findViewById(R.id.Y_plus);
        Y_neg = (Button)findViewById(R.id.Y_neg);
        X_pos = (Button)findViewById(R.id.X_pos);
        X_neg = (Button)findViewById(R.id.X_neg);
        origem = (Button)findViewById(R.id.origin);
        stopButton = (Button)findViewById(R.id.stopButton);

        //TextViews
        xAtual = (TextView) findViewById(R.id.xAtual);
        yAtual = (TextView) findViewById(R.id.yAtual);

        //EditTexts
        xDesejado = (EditText) findViewById(R.id.xDesejado);
        yDesejado = (EditText) findViewById(R.id.yDesejado);
        vel = (EditText) findViewById(R.id.vel);

        //new ConnectBT().execute(); //Call the class to connect

        vel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    double speed = Double.parseDouble(vel.getText().toString());
                    BT.writeValue("VEL" + speed + "\n");
                } catch (NumberFormatException e) {
                    vel.setText("1");
                }

            }
        });



        //commands to be sent to bluetooth

        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                send();
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                stop();
            }
        });

        Y_pos.setOnClickListener(new View.OnClickListener() {
            boolean Ypos = false;

            @Override
            public void onClick(View v)
            {
                if(!Ypos){
                    BT.writeValue("MYP\n");
                    Ypos = true;
                    Y_pos.setBackgroundResource(R.drawable.button_clicked);
                } else {
                    stop();
                    Ypos = false;
                    Y_pos.setBackgroundResource(R.drawable.button_notclicked);
                }

            }
        });

        X_pos.setOnClickListener(new View.OnClickListener() {
            boolean Xpos = false;

            @Override
            public void onClick(View v)
            {
                if(!Xpos){
                    BT.writeValue("MXP\n");
                    Xpos = true;
                    X_pos.setBackgroundResource(R.drawable.button_clicked);
                } else {
                    stop();
                    Xpos = false;
                    X_pos.setBackgroundResource(R.drawable.button_notclicked);
                }

            }
        });

        Y_neg.setOnClickListener(new View.OnClickListener() {
            boolean Yneg = false;

            @Override
            public void onClick(View v)
            {
                if(!Yneg){
                    BT.writeValue("MYN\n");
                    Yneg = true;
                    Y_neg.setBackgroundResource(R.drawable.button_clicked);
                } else {
                    stop();
                    Yneg = false;
                    Y_neg.setBackgroundResource(R.drawable.button_notclicked);
                }

            }
        });

        X_neg.setOnClickListener(new View.OnClickListener() {
            boolean Xneg = false;

            @Override
            public void onClick(View v)
            {
                if(!Xneg){
                    BT.writeValue("MXN\n");
                    X_neg.setFocusable(true);
                    Xneg = true;
                    X_neg.setBackgroundResource(R.drawable.button_clicked);
                } else {
                    stop();
                    X_neg.setFocusable(false);
                    Xneg = false;
                    X_neg.setBackgroundResource(R.drawable.button_notclicked);
                }

            }
        });


    }

    private void stop(){
        BT.writeValue("STOP\n");
    }

    private void Disconnect() {

        BT.disconnect();

        finish(); //return to the first layout
    }

    private void send() {

        double speed = Double.parseDouble(vel.getText().toString());

        BT.writeValue("X" + xDesejado.getText().toString()+"\n");

        BT.writeValue("Y" + yDesejado.getText().toString()+"\n");

        BT.writeValue("AV" + speed + "\n");
    }

    private void msg(String s) {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }


}
