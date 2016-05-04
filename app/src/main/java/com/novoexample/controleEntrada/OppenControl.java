package com.novoexample.controleEntrada;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.content.Intent;
import android.widget.Button;
import android.widget.TextView;

public class OppenControl extends AppCompatActivity {

    Button btnOn, btnOff, btnDis;
    String address = null;
    static TextView statusMessage;
    ConnectionThread connect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //receive the address of the bluetooth device
        Intent newint = getIntent();
        address = newint.getStringExtra(MainActivity.EXTRA_ADDRESS);
        //view of the ledControl layout
        setContentView(R.layout.activity_oppen_control);
        //call the widgtes
        btnOn = (Button) findViewById(R.id.button_onLed);
        btnOff = (Button) findViewById(R.id.button_offLed);
        btnDis = (Button) findViewById(R.id.button_disconnect);
        statusMessage = (TextView) findViewById(R.id.statusMessageOppen);
        connect = new ConnectionThread(address);
        connect.start();
    }

    public void turnOnLed(View view) {
        byte[] data = "l".toString().getBytes();
        connect.write(data);
    }

    public void turnOffLed(View view) {
        byte[] data = "d".toString().getBytes();
        connect.write(data);
    }

    public void Disconnect(View view) {
        connect.cancel();
        finish(); //return to the first layout}
    }

    public static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            Bundle bundle = msg.getData();
            byte[] data = bundle.getByteArray("data");
            String dataString= new String(data);

            if(dataString.equals("---N"))
                statusMessage.setText("Ocorreu um erro durante a conexão D:");
            else if(dataString.equals("---S"))
                statusMessage.setText("Conectado :D");
        }
    };

}