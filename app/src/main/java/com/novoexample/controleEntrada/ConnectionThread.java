package com.novoexample.controleEntrada;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.UUID;

public class ConnectionThread extends Thread{

    BluetoothSocket btSocket = null;
    InputStream input = null;
    OutputStream output = null;
    String btDevAddress = null;
    String myUUID = "00001101-0000-1000-8000-00805F9B34FB";
    boolean running = false;

    public ConnectionThread(String btDevAddress) {
        this.btDevAddress = btDevAddress;
    }

    public void run() {
        // Conectar os dispositivos

        this.running = true;
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();

        try {

            /*  Obtem uma representação do dispositivo Bluetooth com endereço btDevAddress.
                Cria um socket Bluetooth.
             */
            BluetoothDevice btDevice = btAdapter.getRemoteDevice(btDevAddress);
            btSocket = btDevice.createRfcommSocketToServiceRecord(UUID.fromString(myUUID));

            /*  Envia ao sistema um comando para cancelar qualquer processo de descoberta em execução.*/
            btAdapter.cancelDiscovery();

            /*  Solicita uma conexão ao dispositivo cujo endereço é btDevAddress.
                Permanece em estado de espera até que a conexão seja estabelecida.*/
            if (btSocket != null)
                btSocket.connect();

        } catch (IOException e) {

            /*  Caso ocorra alguma exceção, exibe o stack trace para debug.
                Envia um código para a Activity oppenControl, informando que a conexão falhou.*/
            e.printStackTrace();
            toMainActivity("---N".getBytes());
        }

        // Gerenciamento de conexão.

        if(btSocket != null) {

            //Envia um código para a Activity oppenControl informando que a conexão ocorreu com sucesso.
            toMainActivity("---S".getBytes());

            try {

                /*  Obtem referências para os fluxos de entrada e saída do
                socket Bluetooth.
                 */
                input = btSocket.getInputStream();
                output = btSocket.getOutputStream();

                /*  Cria um array de byte para armazenar temporariamente uma msg recebida.
                    O inteiro bytes representará o número de bytes lidos na                 última mensagem recebida.
                 */
                byte[] buffer = new byte[1024];
                int bytes;

                /*  Permanece em estado de espera até que uma mensagem seja recebida.
                    Armazena a mensagem recebida no buffer.
                    Envia a mensagem recebida para a Activity principal, do primeiro ao último byte lido.
                    Esta thread permanecerá em estado de escuta até que a variável running assuma o valor false.
                 */
                while(running) {

                    bytes = input.read(buffer);
                    toMainActivity(Arrays.copyOfRange(buffer, 0, bytes));

                }

            } catch (IOException e) {
                e.printStackTrace();
                toMainActivity("---N".getBytes());
            }
        }

    }

    /*  Utiliza um handler para enviar um byte array à Activity oppenControl.
        O byte array é encapsulado em um Bundle e posteriormente em uma Message
    antes de ser enviado.
     */
    private void toMainActivity(byte[] data) {
        Message message = new Message();
        Bundle bundle = new Bundle();
        bundle.putByteArray("data", data);
        message.setData(bundle);
        OppenControl.handler.sendMessage(message);
    }

    /*  Método utilizado pela Activity principal para transmitir uma mensagem ao
     outro lado da conexão.
        A mensagem deve ser representada por um byte array.
     */
    public void write(byte[] data) {

        if(output != null) {
            try {
                output.write(data);

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            toMainActivity("---N".getBytes());
        }
    }

    //Método utilizado pela Activity principal para encerrar a conexão.
    public void cancel() {

        try {
            running = false;
            btSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        running = false;
    }
}
