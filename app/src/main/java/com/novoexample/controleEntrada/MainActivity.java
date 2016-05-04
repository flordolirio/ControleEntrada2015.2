package com.novoexample.controleEntrada;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.OutputStream;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    Button button_on, button_off, button_visible, button_devices;
    ListView lv;
    //variables to control bluetooth:
    private BluetoothAdapter BA;
    private Set<BluetoothDevice> pairedDevices;
    private OutputStream outStream = null;
    public static String EXTRA_ADDRESS = "device_address";
    public static int ENABLE_BLUETOOTH = 1;
    public static int SELECT_PAIRED_DEVICE = 2;
    public static int SELECT_DISCOVERED_DEVICE = 3;

    static TextView statusMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button_on = (Button)findViewById(R.id.button_on);
        button_off = (Button)findViewById(R.id.button_off);
        button_visible = (Button)findViewById(R.id.button_visible);
        button_devices = (Button)findViewById(R.id.button_devices);
        statusMessage = (TextView) findViewById(R.id.statusMessage);

        BA = BluetoothAdapter.getDefaultAdapter(); // hardware bluetooth em funcionamento
    }

    public void on(View v){
        if (!BA.isEnabled()){//verifica se o adaptador Bluetooth está ativado, senão estiver...
            //Ask to the user turn the bluetooth on
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE); //envia uma solicitação ao sistema na forma de um Intent.
            startActivityForResult(turnOn, ENABLE_BLUETOOTH);
            statusMessage.setText("Solicitando ativação do Bluetooth...");
        }
        else{
            statusMessage.setText("Bluetooth já ativado!");
            //Toast.makeText(getApplicationContext(),"Already on", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Espera que o usuário responda à solicitação, para então decidir o que fazer.

        if(requestCode == ENABLE_BLUETOOTH) {//identificador sobre qual Activity está retornando um resultado, é definido na chamada do método startActivityForResult().
            if(resultCode == RESULT_OK) {//traz a informação sobre a decisão do usuário RESULT_OK : adaptador Bluetooth foi ativado.
                statusMessage.setText("Bluetooth ativado!");
            }
            else {
                statusMessage.setText("Bluetooth não ativado!");
            }
        }
        else if(requestCode == SELECT_PAIRED_DEVICE || requestCode == SELECT_DISCOVERED_DEVICE  ) {
            if(resultCode == RESULT_OK) {
                statusMessage.setText("Você selecionou " + data.getStringExtra("btDevName") + "\n"
                        + data.getStringExtra("btDevAddress"));

                String address = data.getStringExtra("btDevAddress");
                Intent i = new Intent(MainActivity.this, OppenControl.class);

                //Change the activity.
                i.putExtra(EXTRA_ADDRESS, address);
                //this will be received at ledControl (class) Activity
                startActivity(i);
            }
            else {
                statusMessage.setText("Nenhum dispositivo selecionado!");
            }
        }
    }

    public void off(View v){
        BA.disable();
        statusMessage.setText("Desativando Bluetooth...");
    }

    public  void visible(View v){
        Intent getVisible = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        getVisible.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 30);
        startActivity(getVisible);
    }

    public void searchPairedDevices(View view) {

        Intent searchPairedDevicesIntent = new Intent(this, PairedDevices.class);
        startActivityForResult(searchPairedDevicesIntent, SELECT_PAIRED_DEVICE);
    }

    public void discoverDevices(View view) {

        Intent searchPairedDevicesIntent = new Intent(this, DiscoveredDevices.class);
        startActivityForResult(searchPairedDevicesIntent, SELECT_DISCOVERED_DEVICE);
    }

}
